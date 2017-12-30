package com.learning.rxjava.introtorxtutorials.part3_taming_sequence

import android.util.Log
import com.learning.rxjava.introtorxtutorials.BaseRxObs
import com.learning.rxjava.introtorxtutorials.DisplayConsumer
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.ReplaySubject


class TamingSequence : BaseRxObs() {

    /**
     * SIDE EFFECTS
     *
     * Side effects can be very useful and are unavoidable in many cases. But they also have pitfalls.
     * Rx developers are encouraged to avoid unnecessary side effects, and to have a clear intention
     * when they do use them. While some cases are justified, abuse introduces unnecessary hazards.
     *
     *
     * The simple actions coders can take are to reduce the visibility or scope of state and to
     * make what you can immutable. You can reduce the visibility of a variable by scoping it to
     * a code block like a method. You can reduce visibility of class members by making them
     * private or protected. By definition immutable data can't be modified so cannot
     * exhibit side effects.
     */

    /**
     * doOn* functions in RxJava2
     *
     * public final Observable<T> doOnComplete(Action0 onComplete)
     * public final Observable<T> doOnEach(Action1<Notification<? super T>> onNotification)
     * public final Observable<T> doOnEach(Observer<? super T> observer)
     * public final Observable<T> doOnError(Action1<java.lang.Throwable> onError)
     * public final Observable<T> doOnNext(Action1<? super T> onNext)
     * public final Observable<T> doOnTerminate(Action0 onTerminate)
     *
     * They also return the Observable<T>, which means that we can use them between operators
     * in our pipeline. In some cases, you could achieve the same result using map or filter.
     * Using doOn* is better because it documents your intention to have a side effect.
     */


    fun tryDoOnOperators() {
        val values = Observable.just("baby", "teenager", "adult", 5)
                .doOnError( { Log.i(TAG, "doOnError happened")} ) // runs when the observable terminates with an error
                .doOnEach { it -> Log.i(TAG, "doOnEach : " + it.value) } // (1) runs when any notification is emitted
                .doOnNext { it -> Log.i(TAG, "doOnNext : " + it) } // (2) runs when a value is emitted
                .doOnComplete { Log.i(TAG, "doOnComplete reached") } // (4) runs when the observable terminates with no error
                .doOnTerminate { Log.i(TAG, "doOnTerminate reached") } // (5) runs when the observable terminates
                .doFinally { Log.i(TAG, "doFinally reached") } // (6) runs immediately after the observable terminates

        disposable.add(values
                .map { it -> it.equals("adult")}
                .subscribe(DisplayConsumer("Subscribe"))) // (3) runs when value is consumed after being emitted
    }


    fun trySubAndDisposeEvents() {
        val subject: ReplaySubject<Int> = ReplaySubject.create()
        val values = subject
                .doOnSubscribe { Log.i(TAG, "new subscription")  }
                .doOnDispose { Log.i(TAG, "existing subscription is disposed") }

        val disposable1: Disposable = values
                .subscribe(DisplayConsumer("1st subscription"))

        subject.onNext(0)
        subject.onNext(1)

        val disposable2: Disposable = values
                .subscribe(DisplayConsumer("2nd subscription"))

        subject.onNext(2)
        disposable1.dispose()
        subject.onNext(3)
        subject.onNext(4)
        subject.onComplete()
    }

    /**
     * Esposing your Subject reference is unsafe as anyone can call onNext on our Subject and
     * inject values in our sequence.
     *
     * hide() Hides the identity of this Observable and its Disposable. Returned is an Observable
     * which is safe to expose because Observable is immutable.
     */
    fun exposeSubjectAsObs() : Observable<Int> {
        val subject: ReplaySubject<Int> = ReplaySubject.create()


        return subject.hide()
    }

}