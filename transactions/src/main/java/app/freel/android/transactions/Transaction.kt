package app.freel.android.transactions

typealias Mapper<A, B> = suspend (A) -> B
typealias Reverser = suspend () -> Unit

//const val TAG = "Transaction"

internal class TransactionException(val e: Throwable, var reversed: Boolean = false) : Exception(e)

interface ReversibleStep {
    val reverseThis: Reverser
}

interface ReversePastSteps {
    val reversePast: Reverser
}

interface FinalStep<A, B> : PartialStep<A, B>, ReversePastSteps

interface Step<A, B> : PartialStep<A, B>, ReversibleStep, ReversePastSteps

interface PartialStep<A, B> {
    val tag: String
    suspend operator fun invoke(input: A): B
}

private class PartialStepImpl<A, B>(
    override val tag: String,
    private val f: Mapper<A, B>
) : PartialStep<A, B> {
    override suspend fun invoke(input: A): B =
        try {
            //println("$TAG: invoke: $tag input $input")
            val output = f(input)
            //println("$TAG: invoke: $tag output $output")
            output
        } catch (e: Throwable) {
            //println("$TAG: invoke: $tag exception, raising TransactionException")
            throw TransactionException(e)
        }
}

private open class FinalStepImpl<A, B>(
    override val tag: String,
    private val f: Mapper<A, B>,
    override val reversePast: Reverser
): FinalStep<A, B> {

    override suspend fun invoke(input: A): B =
        try {
            //println("$TAG: invoke: $tag input $input")
            val output = f(input)
            //println("$TAG: invoke: $tag output $output $reversePast")
            output
        } catch (e: TransactionException) {
            //println("$TAG: invoke: $tag TransactionException, caught with ${e.reversed}")
            if (!e.reversed) {
                reversePast()
                e.reversed = true
            }
            throw e
        } catch (e: Throwable) {
            //println("$TAG: invoke: $tag exception, raising TransactionException")
            throw TransactionException(e)
        }
}

private class StepImpl<A, B>(
    override val tag: String,
    f: Mapper<A, B>,
    override val reversePast: Reverser,
    override val reverseThis: Reverser
): FinalStepImpl<A, B>(tag, f, reversePast), Step<A, B>

private infix fun Reverser.compose(other: Reverser): Reverser = {
    this()
    other()
}

private infix fun <A, B, C> Mapper<A, B>.compose(other: Mapper<B, C>): Mapper<A, C> = {
    other(this(it))
}

infix fun <A, B, C> Step<A, B>.then(step: Step<B, C>): Step<A, C> =
    StepImpl("${tag}_${step.tag}",
        ::invoke compose step::invoke,
        reverseThis compose reversePast,
        step.reverseThis)

infix fun <A, B, C> Step<A, B>.then(step: PartialStep<B, C>): FinalStep<A, C> =
    FinalStepImpl("${tag}_${step.tag}",
        ::invoke compose step::invoke,
        reverseThis compose reversePast)

fun <A, B> PartialStep<A, B>.reversibleWith(reverse: Reverser): Step<A, B> =
    StepImpl("${tag}_with_reversible",
        ::invoke,
        { //println("$TAG: ${tag}_with_reversible reversibleWith: empty")
        },
        reverse)

//fun <A, B> transaction(step: () -> TransactionStep<A, B>) = step()

//fun <B> transaction(step: () -> TransactionStep<Unit, B>) = step

suspend fun <A, B> runTransaction(
    input: A,
    _step: () -> FinalStep<A, B>
): Either<A, B> {
    val step = _step()

    return try {
        Either.Right(step(input))
    } catch (e: TransactionException) {
        //println("$TAG: MyTransaction2: exception $e")
        if (!e.reversed) step.reversePast()
        Either.Left(input)
    }
}

suspend fun <B> runTransaction(step: () -> FinalStep<Unit, B>): Either<Unit, B> =
    runTransaction(Unit, step)

fun <A, B> step(tag: String = "no_tag", f: suspend A.() -> B): PartialStep<A, B> = PartialStepImpl(tag, f)

//fun <A, B> finalStep(f: suspend A.() -> B): PartialStep<A, B> = PartialStep(f)

sealed class Either<out A, out B> {
    class Left<A>(val leftValue: A): Either<A, Nothing>()
    class Right<B>(val rightValue: B): Either<Nothing, B>()
}

val <A, B> Either<A, B>.value get(): Any? = when (this) {
    is Either.Left -> leftValue
    is Either.Right -> rightValue
}

val <A, B> Either<A, B>.successOrNull get(): B? = when (this) {
    is Either.Left -> null
    is Either.Right -> rightValue
}