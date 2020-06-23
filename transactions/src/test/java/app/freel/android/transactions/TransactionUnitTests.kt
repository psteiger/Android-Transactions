package app.freel.android.transactions

import kotlinx.coroutines.runBlocking
import org.junit.Test

import org.junit.Assert.*
import java.lang.Exception

class TransactionUnitTests {
    @Test
    fun fourStepTransaction_isCorrect() {
        runBlocking {
            var int = 7
            val transactionResult = runTransaction(7) {
                step<Int, Int>("step1") {
                    //println("$TAG: MyTransaction2 summing 10: $int")
                    int += 10
                    //println("$TAG: MyTransaction2 done summing 10: $int")
                    assertEquals(17, int)
                    assertEquals(7, this)
                    int
                }.reversibleWith {
                    //println("$TAG: MyTransaction2 reversing summing 10: $int")
                    int -= 10
                    //println("$TAG: MyTransaction2 done reversing summing 10: $int")
                } then step<Int, Int>("step2") {
                    //println("$TAG: MyTransaction2 multiplying 20: $int")
                    assertEquals(17, this)
                    int *= 20
                    assertEquals(340, int)
                    //println("$TAG: MyTransaction2 done multiplying 20: $int")
                    int
                }.reversibleWith {
                    //println("$TAG: MyTransaction2 reversing multiply 20: $int")
                    int /= 20
                    //println("$TAG: MyTransaction2 done reversing multiply 20: $int")
                } then step<Int, Float>("step3") {
                    //println("$TAG: MyTransaction2 converting to float: $int")
                    toFloat()
                    //throw java.lang.IllegalStateException()
                }.reversibleWith {
                    //println("$TAG: MyTransaction2 reversing to float: $int")
                    //println("$TAG: MyTransaction2 done reversing to float: $int")
                } then step<Float, String>("step4") {
                    //println("$TAG: MyTransaction2 finishing: $int")
                    assertEquals(340f, this)
                    "acabou $this"
                }
            }

            assertEquals(transactionResult.value, "acabou 340.0")
        }

    }

    @Test
    fun fourStepTransactionFailingAtFirstStepReversal_isCorrect() {
        runBlocking {
            var int = 7
            val transactionResult = runTransaction(7) {
                step<Int, Unit>("step1") {
                    //println("$TAG: MyTransaction2 throwing exception: $int")
                    throw java.lang.IllegalStateException()
                    //println("$TAG: MyTransaction2 summing 10: $int")
                    int += 10
                    //println("$TAG: MyTransaction2 done summing 10: $int")
                }.reversibleWith {
                    //println("$TAG: MyTransaction2 reversing summing 10: $int")
                    int -= 10
                    //println("$TAG: MyTransaction2 done reversing summing 10: $int")
                } then step<Unit, Unit>("step2") {
                    //println("$TAG: MyTransaction2 multiplying 20: $int")
                    int *= 20
                    //println("$TAG: MyTransaction2 done multiplying 20: $int")
                }.reversibleWith {
                    //println("$TAG: MyTransaction2 reversing multiply 20: $int")
                    int /= 20
                    //println("$TAG: MyTransaction2 done reversing multiply 20: $int")
                } then step<Unit, Float>("step3") {
                    //println("$TAG: MyTransaction2 converting to float: $int")
                    int.toFloat()
                    //throw java.lang.IllegalStateException()
                }.reversibleWith {
                    //println("$TAG: MyTransaction2 reversing to float: $int")
                    int
                    //println("$TAG: MyTransaction2 done reversing to float: $int")
                } then step<Float, String>("step4") {
                    //println("$TAG: MyTransaction2 finishing: $int")
                    "acabou $this"
                }
            }

            assertEquals(transactionResult.value, 7)
        }

    }

    @Test
    fun fourStepTransactionFailingAtSecondStepReversal_isCorrect() {
        runBlocking {
            var int = 7
            val transactionResult = runTransaction(7) {
                step<Int, Unit>("step1") {
                    //println("$TAG: MyTransaction2 summing 10: $int")
                    int += 10
                    //println("$TAG: MyTransaction2 done summing 10: $int")
                }.reversibleWith {
                    //println("$TAG: MyTransaction2 reversing summing 10: $int")
                    int -= 10
                    //println("$TAG: MyTransaction2 done reversing summing 10: $int")
                } then step<Unit, Unit>("step2") {
                    //println("$TAG: MyTransaction2 throwing exception: $int")
                    throw java.lang.IllegalStateException()
                    //println("$TAG: MyTransaction2 multiplying 20: $int")
                    int *= 20
                    //println("$TAG: MyTransaction2 done multiplying 20: $int")
                }.reversibleWith {
                    //println("$TAG: MyTransaction2 reversing multiply 20: $int")
                    int /= 20
                    //println("$TAG: MyTransaction2 done reversing multiply 20: $int")
                } then step<Unit, Float>("step3") {
                    //println("$TAG: MyTransaction2 converting to float: $int")
                    int.toFloat()
                    //throw java.lang.IllegalStateException()
                }.reversibleWith {
                    //println("$TAG: MyTransaction2 reversing to float: $int")
                    int
                    //println("$TAG: MyTransaction2 done reversing to float: $int")
                } then step<Float, String>("step4") {
                    //println("$TAG: MyTransaction2 finishing: $int")
                    "acabou $this"
                }
            }

            assertEquals(transactionResult.value, 7)
        }

    }

    @Test
    fun fourStepTransactionFailingAtThirdStepReversal_isCorrect() {
        runBlocking {
            var int = 7
            val transactionResult = runTransaction(7) {
                step<Int, Unit>("step1") {
                    //println("$TAG: MyTransaction2 summing 10: $int")
                    int += 10
                    //println("$TAG: MyTransaction2 done summing 10: $int")
                }.reversibleWith {
                    //println("$TAG: MyTransaction2 reversing summing 10: $int")
                    int -= 10
                    //println("$TAG: MyTransaction2 done reversing summing 10: $int")
                } then step<Unit, Unit>("step2") {
                    //println("$TAG: MyTransaction2 multiplying 20: $int")
                    int *= 20
                    //println("$TAG: MyTransaction2 done multiplying 20: $int")
                }.reversibleWith {
                    //println("$TAG: MyTransaction2 reversing multiply 20: $int")
                    int /= 20
                    //println("$TAG: MyTransaction2 done reversing multiply 20: $int")
                } then step<Unit, Float>("step3") {
                    //println("$TAG: MyTransaction2 throwing exception: $int")
                    throw java.lang.IllegalStateException()
                    //println("$TAG: MyTransaction2 converting to float: $int")
                    int.toFloat()
                    //throw java.lang.IllegalStateException()
                }.reversibleWith {
                    //println("$TAG: MyTransaction2 reversing to float: $int")
                    int
                    //println("$TAG: MyTransaction2 done reversing to float: $int")
                } then step<Float, String>("step4") {
                    //println("$TAG: MyTransaction2 finishing: $int")
                    "acabou $this"
                }
            }

            assertEquals(transactionResult.value, 7)
        }

    }

    @Test
    fun fourStepTransactionFailingAtFourthStepReversal_isCorrect() {
        runBlocking {
            var int = 7
            val transactionResult = runTransaction(7) {
                step<Int, Unit>("step1") {
                    //println("$TAG: MyTransaction2 summing 10: $int")
                    int += 10
                    //println("$TAG: MyTransaction2 done summing 10: $int")
                }.reversibleWith {
                    //println("$TAG: MyTransaction2 reversing summing 10: $int")
                    int -= 10
                    //println("$TAG: MyTransaction2 done reversing summing 10: $int")
                } then step<Unit, Unit>("step2") {
                    //println("$TAG: MyTransaction2 multiplying 20: $int")
                    int *= 20
                    //println("$TAG: MyTransaction2 done multiplying 20: $int")
                }.reversibleWith {
                    //println("$TAG: MyTransaction2 reversing multiply 20: $int")
                    int /= 20
                    //println("$TAG: MyTransaction2 done reversing multiply 20: $int")
                } then step<Unit, Float>("step3") {
                    //println("$TAG: MyTransaction2 converting to float: $int")
                    int.toFloat()
                    //throw java.lang.IllegalStateException()
                }.reversibleWith {
                    //println("$TAG: MyTransaction2 reversing to float: $int")
                    int
                    //println("$TAG: MyTransaction2 done reversing to float: $int")
                } then step<Float, String>("step4") {
                    //println("$TAG: MyTransaction2 throwing exception: $int")
                    throw java.lang.IllegalStateException()
                    //println("$TAG: MyTransaction2 finishing: $int")
                    "acabou $this"
                }
            }

            assertEquals(transactionResult.value, 7)
        }

    }

    @Test
    fun twoStepTransaction_isSuccess() {
        runBlocking {
            var int = 10
            val transactionResult = runTransaction(int) {
                step<Int, Int> {
                    int += 5
                    int
                }.reversibleWith {
                    int -= 5
                } then step<Int, Int> {
                    int *= 10
                    int
                }
            }

            assertEquals(150, transactionResult.value)
        }
    }

    @Test
    fun twoStepTransactionFailureAtFirstStepReversal_isSuccess() {
        runBlocking {
            var int = 10
            val transactionResult = runTransaction(int) {
                step<Int, Int> {
                    throw Exception()
                    int += 5
                    int
                }.reversibleWith {
                    int -= 5
                } then step<Int, Int> {
                    int *= 10
                    int
                }
            }

            assertEquals(10, transactionResult.value)
        }
    }

    @Test
    fun twoStepTransactionFailureAtSecondStepReversal_isSuccess() {
        runBlocking {
            var int = 10
            val transactionResult = runTransaction(int) {
                step<Int, Int> {
                    int += 5
                    int
                }.reversibleWith {
                    int -= 5
                } then step<Int, Int> {
                    throw Exception()
                    int *= 10
                    int
                }
            }

            assertEquals(10, transactionResult.value)
        }
    }
}