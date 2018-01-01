package com.learning.rxjava

import com.learning.rxjava.introtorxtutorials.BaseRxObs
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.TestScheduler
import io.reactivex.subjects.PublishSubject
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


class UnitTestingRx : BaseRxObs() {

    /**
     * To support our internal testing, all base reactive types now feature test() methods (which
     * is a huge convenience for us) returning TestSubscriber or TestObserver
     */
    @Test
    fun testBasicOversvable() {
        Observable.just(1, 2)
                .test()
                .assertValueAt(0, 1)
                .assertValueAt(1, 2)
    }

    /**
     * non-backpressured Observable, Single,  Maybe and Completable can be tested with
     * io.reactivex.observers.TestObserver
     */
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
    fun testWithSingle() {
        val testObserver = TestObserver<Int>()
        Single.create<Int> {
            emitter -> emitter.onSuccess(100) }
                .subscribe(testObserver)

        testObserver.assertSubscribed()
                .assertValue(100)
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

    /**
     * Rx operators which involve asynchronous actions schedule those actions using a scheduler.
     * If you take a look at all the operators in Observable, you will see that such operators
     * have overloads that take a scheduler. This is the way that you can supplement their real-time
     * schedulers for your TestScheduler.
     */
    @Test
    fun testWithTestScheduler() {
        val scheduler = TestScheduler()
        val expected = Arrays.asList(0L, 1L, 2L, 3L, 4L)
        val result = ArrayList<Long>()
        Observable
                .interval(1, TimeUnit.SECONDS, scheduler)
                .take(5)
                .subscribe { i -> result.add(i) }
        assertTrue(result.isEmpty())
        scheduler.advanceTimeBy(5, TimeUnit.SECONDS)
        assertTrue(result == expected)
    }

    @Test
    fun testPublishSubject() {
        val subject = PublishSubject.create<Int>()


        // nobody subscribed yet
        assertFalse(subject.hasObservers())

        subject.subscribe()
        subject.onNext(5)


        //There is a subscription now
        assertTrue(subject.hasObservers())
        subject.test().assertSubscribed()


        // nobody remained subscribed because it is disposed
        subject.test(true).assertSubscribed()
    }

    /**
     * This is useful for testing small, self-contained pieces of Rx code, such as custom operators.
     * A complete system may be using schedulers on its own, thus defeating our virtual time.
     *
     * When in debug-mode, our custom scheduler factory will replace all schedulers with a
     * TestScheduler, which we will then use to control time throughout our system.
     */
    @Test
    fun testWithTestObserverAndInterval() {
        val scheduler = TestScheduler()
        val testObserver = TestObserver<Long>()
        val expected = Arrays.asList(0L, 1L, 2L, 3L, 4L)
        Observable
                .interval(1, TimeUnit.SECONDS, scheduler)
                .take(5)
                .subscribe(testObserver)
        testObserver.assertEmpty()
        scheduler.advanceTimeBy(5, TimeUnit.SECONDS)
        testObserver.assertValueSequence(expected)
    }

}
