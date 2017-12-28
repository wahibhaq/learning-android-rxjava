package com.learning.rxjava.introtorxtutorials.part3

import android.util.Log
import com.learning.rxjava.introtorxtutorials.BaseRxObs
import com.learning.rxjava.introtorxtutorials.DisplayConsumer
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import io.reactivex.functions.Action

/**
 * Both lift and compose are meta-operators, used for injecting a custom operator into the chain.
 *
 * If the custom operator is a composite of existing operators, compose() is a natural fit.
 * If the custom operator needs to extract values from the pipeline to process them and
 * then push them back, lift() is a better fit.
 */
class CustomOperators : BaseRxObs() {

    private val values = Observable.range(1, 20)

    /**
     * The following example shows how you can use the compose( ) operator to chain your custom operator
     * and make it reusable.
     *
     * The difference between compose() and flatmap() is that compose() is a higher level abstraction:
     * it operates on the entire stream, not individually emitted items.
     */
    fun calculateAverage() {
        disposable.add(values
                .compose(RunningAverage())
                .subscribeWith(stringDisposableObserver()))
    }

    /**
     * The lift operator is similar to compose, with the difference of transforming a Subscriber,
     * instead of an Observable.
     *
     * If the operator you are creating is designed to act on the individual items emitted by a source
     * ObservableSource, use {@code lift}. If your operator is designed to transform the source ObservableSource as a whole
     * (for instance, by applying a particular set of existing RxJava operators to it) use {@link #compose}.
     *
     * Standard operators are also implemented using lift, which makes lift a hot method at runtime.
     * JVM optimises for lift and operators that use lift receive a performance boost.
     */
    fun operatorTargetingLift() {
        disposable.add(
                Observable.just("Wahib", "ul", "Haq")
                        .lift(FindWorldLengthOperator({ t: String -> t.length }))
                        .subscribeWith(intDisposableObserver())
        )
    }

    /**
     * Implementing your own Transformer
     *
     * Transformer is actually just Func1<Observable<T>, Observable<R>>. In other words: feed it an
     * Observable of one type and it'll return an Observable of another.
     */
    private class RunningAverage : ObservableTransformer<Int, kotlin.String> {

        private class AverageAcc(val sum: Int, val count: Int)

        override fun apply(source: Observable<Int>): ObservableSource<String> {
            return source
                    .scan(AverageAcc(0,0),
                            { acc: AverageAcc, v: Int ->
                                AverageAcc(acc.sum + v, acc.count + 1) })
                    .filter { acc: AverageAcc -> acc.sum > 0 }
                    .map { acc: AverageAcc -> acc.count.toString() + " : " + acc.sum.div(acc.count).toString() }
        }
    }

    /**
     * If you can't guarantee that your operator will obey the Rx contract, for example because
     * you push asynchronously from multiple sources, you can use the serialize operator.
     *
     * The serialize operator will turn an unreliable observable into a lawful, sequential observable.
     */
    fun serializeObservable() {
        val source = Observable.create<Int> { o ->
            o.onNext(1)
            o.onNext(2)
            o.onComplete()
            o.onNext(3)
            o.onComplete()
        }
                .serialize()

        source
                .doFinally { Log.i(TAG, "Unsubscribed") }
                .subscribe(DisplayConsumer("Next"), DisplayConsumer("Error"),
                        Action { Log.i(TAG, "Completed") })
    }
}