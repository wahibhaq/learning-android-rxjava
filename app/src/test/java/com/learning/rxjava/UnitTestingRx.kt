package com.learning.rxjava

import android.nfc.Tag
import android.util.Log
import com.learning.rxjava.introtorxtutorials.BaseRxObs
import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.TestScheduler
import org.junit.Test
import java.util.concurrent.TimeUnit

class UnitTestingRx : BaseRxObs() {

    @Test
    fun testBasicOversvable() {
        Observable.just(1, 2)
                .test()
                .assertValueAt(0, 1)
                .assertValueAt(1, 2)
    }

    @Test
    fun testWithTestObserver() {
        val testObserver = TestObserver<Int>()
        Observable.just(1)
                .subscribe(testObserver)

        testObserver.assertValues(1)
                .assertSubscribed()
                .assertComplete()
                .assertNoErrors()
    }

    @Test
    fun testSentValuesAndError() {
        Observable.error<Int>(RuntimeException())
                .startWith(Observable.just(1, 2))
                .test()
                .assertFailure(RuntimeException::class.java, 1, 2)
    }

    @Test
    fun testBasicMap() {
        Observable.just("haha", "zoyo", "titi")
                .map { it -> it.toUpperCase() }
                .test()
                .assertResult("HAHA", "ZOYO", "TITI")
    }

    /**
     * advanceTimeTo() will execute all actions that are scheduled for up to a specific moment in time.
     * advanceTimeBy() works also in a similar fashion.
     *
     * If there is a time collision and chance of tasks occuring at the same time then note it down that
     * the order that two simultaneous tasks are executed is the same as the order in which they where scheduled.
     */
    @Test
    fun testWithAdvanceTimeTo() {
        val testScheduler = TestScheduler()
        testScheduler.createWorker()
                .schedule({  println("scheduling in 10 s future")},
                        10, TimeUnit.SECONDS)

        println("Advancing to 10 s")
        testScheduler.advanceTimeTo(10, TimeUnit.SECONDS)
        println("Virtual now time: " + testScheduler.now(TimeUnit.SECONDS))
    }

    /**
     * triggerActions does not advance time. It only executes actions that were scheduled
     * to be executed up to the present.
     */
    @Test
    fun testWithTriggerActions() {
        val testScheduler = TestScheduler()
        testScheduler.scheduleDirect({  println("scheduling for immediate")})
        testScheduler.scheduleDirect({  println("scheduling in 20 s future")},
                20, TimeUnit.SECONDS)

        testScheduler.triggerActions()
        println("Virtual now time: " + testScheduler.now(TimeUnit.SECONDS))
    }


}
