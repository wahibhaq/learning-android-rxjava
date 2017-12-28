package com.learning.rxjava.introtorxtutorials.part3;


import io.reactivex.ObservableOperator;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import kotlin.jvm.functions.Function1;

/**
 * If you studied the signature, there's something which seems backwards at first: to turn an
 * Observable<String> into Observable<Integer>, we need a function that turns Observer<Integer>
 * into Observer<String>. To understand why that is the case, remember that a subscription begins
 * at the end of the chain and is propagated to the source. In other words, a subscription goes
 * backwards through the chain of operators. Each operator receives a subscription (i.e. is subscribed to) and uses that
 * subscription to create a subscription to the preceeding operator.
 */
class FindWorldLengthOperator implements ObservableOperator<Integer, String> {

    private Function1<String, Integer> transformer;

    public FindWorldLengthOperator(Function1<String, Integer> transformer) {
        this.transformer = transformer;
    }

    @Override
    public Observer<? super java.lang.String> apply(Observer<? super java.lang.Integer> child) throws Exception {
        return new Observer<java.lang.String>() {
            @Override
            public void onSubscribe(Disposable d) {
                child.onSubscribe(d);
            }

            @Override
            public void onNext(java.lang.String s) {
                child.onNext(transformer.invoke(s));
            }

            @Override
            public void onError(Throwable e) {
                child.onError(e);
            }

            @Override
            public void onComplete() {
                child.onComplete();
            }
        };
    }
}
