import hu.akarnokd.rxjava2.math.MathObservable;
import io.reactivex.*;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableMaybeObserver;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.schedulers.Timed;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import io.reactivex.subjects.UnicastSubject;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class testRxAnyMore {

    public static void main(String[] args) {
        observableOnSubscribe();
    }

    public static void observableOnSubscribe(){
        // this is used when you want to defer execution until the terminal subscription is attached to the obsevable
        ObservableOnSubscribe<String> naon = emitter -> {

            // you can change it with sequence what you want to do
            List<String> data = new ArrayList<>();
            data.add("Satu");
            data.add("Dua");
            data.add("Tiga");
            data.add("Empat");

            data.forEach( item -> emitter.onNext(item));
            emitter.onComplete();

        };

        Observable.create(naon)
                .observeOn(Schedulers.newThread())
                .subscribe(System.out::println);
    }

    public static void backOffExampleOfRetry(){
        // RetryWhen is used when you face error and you want to run it again
        Observable.error(RuntimeException::new)
                .retryWhen(throwableObservable ->
                        throwableObservable.zipWith(Observable.range(1, 2), (throwable, integer) ->
                                integer).flatMap(i -> Observable.timer(1, TimeUnit.SECONDS)))
                .observeOn(Schedulers.trampoline())
        .subscribe( i -> System.out.println(i));
    }

    public static <T> void ofTypeOperatorExample(Class<T> type){
        Observable.just(1, 2, "A")
                .ofType(type)
                .subscribe(item -> System.out.println(item.toString()));
    }


    public static void sustainableRX(){
        Observable.just(1)
                .map(new Function<Integer, String>() {
                    @Override
                    public String apply(Integer integer) throws Exception {
                        return getSustainable(integer);
                    }
                })
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        System.out.println("Hasil Sustainable : " + s);
                    }
                });
    }

    public static String getSustainable(int value){
        final String[] result = new String[1];

        switch (value){
            case 1 :
                Observable.just("A")
                        .subscribe(new Consumer<String>() {
                            @Override
                            public void accept(String s) throws Exception {
                              result[0] = s;
                            }
                        });
        }
        return result[0] == null ? "kosong" : result[0];
    }

    public static void addInteg(List<? super Integer> list){
        list.add(new Integer(2));
    }

    public static void printData(List<?> data){
        for (Object b : data)
            System.out.println(b + " :: ");
    }

    public static <T extends Number> T sum(List<T> list){
        T sum = list.get(0);

        for (T n : list){
            if ((Integer) n> sum.intValue()) {
                sum = n;
            }
        }

        return sum;
    }

    private static void shareOperatorexample(){
        Observable<Long> contoh= Observable
                .just(System.currentTimeMillis())
//                .delay(1,TimeUnit.SECONDS)
//                .publish().autoConnect();
                .share();

        contoh.subscribe(nilai -> System.out.println("Observer 1 nilainya : " + nilai));
        contoh.subscribe(nilai -> System.out.println("Observer 2 nilainya : " + nilai));

//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

//        contoh.connect();
    }

    private static void subscriberExample(){

        final Subscription[] subscription = new Subscription[1];

        Flowable.range(1,10000000)
                .onBackpressureBuffer()
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        //has to use this to get data from observable
                        s.request(100);
                        subscription[0] = s;
                    }

                    @Override
                    public void onNext(Integer integer) {
                        System.out.println("Data " + integer);
                    }

                    @Override
                    public void onError(Throwable t) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private static void PublisherExampler(){
        Observable.fromPublisher(new Publisher<String>() {
            @Override
            public void subscribe(Subscriber<? super String> s) {
                s.onNext("Nama");
            }
        }).subscribe(System.out::println);
    }

    private static void flatMapIterableExample(){

        List<String> listExample = new ArrayList<>();
        listExample.add("Aku");
        listExample.add("Dia");
        listExample.add("Mereka");
        listExample.add("Anda");

        AtomicInteger atomicInteger = new AtomicInteger();

        // Flat Mapt Itterable
        Observable.fromIterable(listExample)
                .flatMap(s -> Observable.just(s + " - Flat Map", s + " Map Flat"))
                .doOnNext(s -> atomicInteger.incrementAndGet())
                .sorted()
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String s) {
                        System.out.println(s);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        System.out.println(atomicInteger.get());
                    }
                });


        //Flat Map Iterable
        getListExample()
                .flatMapIterable(new Function<List<String>, Iterable<String>>() {
                    @Override
                    public Iterable<String> apply(List<String> strings) throws Exception {
                        return strings;
                    }
                })
//                .flatMap(x -> Observable.just(x))
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String s) {
                        System.out.println(s);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private static Observable<List<String>> getListExample(){
        List<String> listExample = new ArrayList<>();
        listExample.add("Aku");
        listExample.add("Dia");
        listExample.add("Mereka");
        listExample.add("Anda");

        return Observable.create(emitter -> emitter.onNext(listExample));
    }

    private static void blockingSubscribeExample(){
        Observable.just(1,2)
                .blockingSubscribe(System.out::println,err -> System.out.println(err.getMessage())
                ,() -> System.out.println(" On Complete"));
    }

    private static void emptyOperatorExample() {
        Observable.empty()
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        System.out.println("On Empty Subscribe");
                    }

                    @Override
                    public void onNext(Object o) {
                        System.out.println("On Empty Next");
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println("On Empty Error");
                    }

                    @Override
                    public void onComplete() {
                        System.out.println("On Empty Complete");
                    }
                });
    }

    private static void flowableCreatied() {
        Flowable.generate(
                () -> 1,
                (current, emit) -> {
                    emit.onNext(1);
                    return 1;
                }
        ).observeOn(Schedulers.single(), false, 1);
    }

    private static void flowableExample() {

        PublishSubject<Integer> publishSource = PublishSubject.create();
        for (int i = 0; i < 100000; i++) {
            publishSource.onNext(i);
        }

        publishSource
                .toFlowable(BackpressureStrategy.LATEST).onBackpressureDrop()
                .observeOn(Schedulers.computation())
                .subscribe(new FlowableSubscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription s) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        System.out.println(integer);
                    }

                    @Override
                    public void onError(Throwable t) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private static void retryWhenExample() {
        AtomicInteger atomicInteger = new AtomicInteger(0);
        Exception noretryException = new Exception("don't retry");

        Observable.error(() -> {
            atomicInteger.incrementAndGet();
            return new IllegalArgumentException();
        })
                //Retry and throw erro
                .retryWhen(throwableObservable -> Observable.error(noretryException)) // will get atomIncrement = 0
                //Retry and call on complete
                .retryWhen(throwableObservable -> Observable.empty())  // will get atomIncrement = 0
                //Retry and re Subscribe
                .retryWhen(throwableObservable -> Observable.just("Anything")) // will get atomIncrement = 1
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        System.out.println("On Subscribe");
                    }

                    @Override
                    public void onNext(Object o) {
                        System.out.println("On Next" + o.toString());
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println("On Error");
                    }

                    @Override
                    public void onComplete() {
                        System.out.println("On Complete");
                    }
                });
        System.out.println(atomicInteger.get());
    }

    private static void retryCondinitionExample() {
        AtomicInteger atomicInteger = new AtomicInteger(0);

        Observable.error(() -> {
            atomicInteger.incrementAndGet();
            return new IllegalArgumentException();
        })
                .retry(((integer, throwable) -> integer < 1))
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        if (o instanceof Exception)
                            System.out.println("Error nih");
                    }
                });

        System.out.println(atomicInteger.get());
    }

    private static void retryErrorHandlerExample() {
        boolean e = false;

        Observable.fromArray(1, 2, 3)
                .doOnNext(err -> {
                    if (err == 3) {
                        throw (new RuntimeException("Exception on 2"));
                    }
                })
                .retry(2)
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        System.out.println("OnNext : " + integer);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private static void hanldeReturnErrorExample() {
        Observable.fromArray(1, 2, 3)
                .doOnNext(err -> {
                    if (err == 2)
                        throw (new RuntimeException("Exception on 2"));
                })
                .onErrorReturn(exp -> {
                    if (exp.getMessage() == "Exception on 2")
                        return 10;
                    else
                        return 2;
                }).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Integer integer) {
                System.out.println("OnNext : " + integer);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private static void hadnleErrorExample() {
        Observable.fromArray(1, 2, 3, 4)
                .doOnNext(emit -> {
                    if (emit == 2)
                        throw (new RuntimeException("Exception On 2"));
                })
                // do something before error Occur
                .doOnError(error -> System.out.println("Before Error"))
                // resume with another resource after error occur
                .onExceptionResumeNext(Observable.just(10))
                // return value if error occur
                .onErrorReturnItem(-1)
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        System.out.println("OnNext : " + integer);
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        System.out.println("On Complete");
                    }
                });
    }

    private static void uniCastSubject() {
        Observable<Integer> observable = Observable.range(1, 5)
                .subscribeOn(Schedulers.trampoline());

        UnicastSubject<Integer> pSubject = UnicastSubject.create();
        observable.subscribe(pSubject);

        pSubject.subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Integer integer) {
                System.out.println("Dari Observer 1: " + integer);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

        pSubject.subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Integer integer) {
                System.out.println("Dari observer ke 2: " + integer);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private static void takeWhile() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                for (int i = 0; i <= 6; i++) {
                    Thread.sleep(1000);
                    emitter.onNext(i);
                }
                emitter.onComplete();
            }
        })
                .doOnEach(new Consumer<Notification<Integer>>() {
                    @Override
                    public void accept(Notification<Integer> integerNotification) throws Exception {
                        System.out.println("onNext Process...");
                    }
                })
                .takeWhile(err -> err < 3)
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        System.out.println("OnNext : " + integer);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private static void takeUntilExample() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                for (int i = 0; i <= 6; i++) {
                    Thread.sleep(1000);
                    emitter.onNext(i);
                }
                emitter.onComplete();
            }
        })
                .takeUntil(Observable.timer(4, TimeUnit.SECONDS).flatMap(
                        em -> Observable.just(11, 12, 13, 14, 15, 16)
                ))
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        System.out.println("OnNext : " + integer);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private static void skipWhileExample() {
        Observable.range(0, 6)
                .delay(2, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.from(Executors.newFixedThreadPool(1)))
                .skipWhile(st -> st < 3)
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        System.out.println("OnNext : " + integer);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private static void skipUntilExample() {
        Observable<Integer> observable1 = Observable.create(emitter -> {
            for (int i = 0; i <= 6; i++) {
//                Thread.sleep(1000);
                emitter.onNext(i);
            }
            emitter.onComplete();
        });

        Observable<Integer> observable2 = Observable.timer(1, TimeUnit.SECONDS)
                .flatMap(__ -> Observable.just(11, 12, 13, 14, 15, 16));

        observable2.skipUntil(observable1)
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        System.out.println("OnNext : " + integer);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private static void sequenceEqual() {
        Observable.sequenceEqual(Observable.just(1, 2, 3, 4, 5), Observable.just(1, 2, 3, 4))
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        System.out.println("Is Observable same ? " + aBoolean);
                    }
                });
    }

    private static void containsExample() {
        Observable.just("A", "B", "C")
                .contains("A")
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        System.out.println("Teradapat itemnya : " + aBoolean);
                    }
                });
    }

    private static void ambExample() {
        Observable<Integer> observable1 = Observable.timer(2, TimeUnit.SECONDS)
                .flatMap(new Function<Long, ObservableSource<Integer>>() {
                    @Override
                    public ObservableSource<Integer> apply(Long aLong) throws Exception {
                        return Observable.just(10, 20, 30, 40, 50);
                    }
                });
        Observable<Integer> observable2 = Observable.timer(3, TimeUnit.SECONDS)
                .flatMap(new Function<Long, ObservableSource<Integer>>() {
                    @Override
                    public ObservableSource<Integer> apply(Long aLong) throws Exception {
                        return Observable.just(100, 200, 300, 400, 500);
                    }
                });

        Observable.ambArray(observable1, observable2).subscribeOn(
                Schedulers.from(Executors.newFixedThreadPool(1)))
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        System.out.println("on Next: " + integer);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private static void allExample() {
        Observable.just("AC", "BC", "C")
                .all(item -> item.contains("C")).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                System.out.println("Status = " + aBoolean);
            }
        });
    }

    private static void sumExample() {
        MathObservable.sumInt(Observable.just(1, 2, 3, 4, 5))
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        System.out.println("onNext : " + integer);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private static void reduceExample() {
        Observable.just(1, 2, 3, 4, 5)
                .reduce((l, o) -> l * o)
                .subscribe(new DisposableMaybeObserver<Integer>() {
                    @Override
                    public void onSuccess(Integer integer) {
                        System.out.println("Is Dispose: " + isDisposed());
                        System.out.println("OnNext : " + integer);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        System.out.println("is Dispose: " + isDisposed());
                        dispose();
                    }
                });
    }

    private static void minExample() {
        MathObservable.min(Observable.just(-1, 2, 0, -9))
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        System.out.println("OnConsume: " + integer);
                    }
                });
    }

    private static void maxExample() {
        MathObservable.max(Observable.just(1, 2, 4))
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        System.out.println("OnNext: " + integer);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private static void countExample() {
        Observable.just(1, 2, 3, 4, 5, 6)
                .doOnComplete(() -> System.out.println("On Complete"))
                .count()
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        System.out.println("onConsume: " + aLong);
                    }
                });
    }

    private static void averageExample() {
        MathObservable.averageDouble(Observable.just(5.34, 6.98))
                .subscribe(new Observer<Double>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Double aDouble) {
                        System.out.println("oNNext : " + aDouble);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private static void timeIntervalExample() {
        Observable.interval(100, TimeUnit.MILLISECONDS)
                .take(3)
                .timeInterval()
                .subscribeOn(Schedulers.from(Executors.newFixedThreadPool(1)))
                .subscribe(new Subject<Timed<Long>>() {
                    @Override
                    public boolean hasObservers() {
                        return false;
                    }

                    @Override
                    public boolean hasThrowable() {
                        return false;
                    }

                    @Override
                    public boolean hasComplete() {
                        return false;
                    }

                    @Override
                    public Throwable getThrowable() {
                        return null;
                    }

                    @Override
                    protected void subscribeActual(Observer<? super Timed<Long>> observer) {

                    }

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Timed<Long> longTimed) {
                        System.out.println("onNext: " + longTimed);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private static void dematerializeExample() {
        Observable<Notification<Integer>> data = Observable.just(1, 2, 4, 5, 6, 7, 8)
                .materialize();

        data.dematerialize().subscribe();
    }

    private static void usingExample() {
        Observable.using(
                new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        return "Example";
                    }
                },
                new Function<String, ObservableSource<Character>>() {
                    @Override
                    public ObservableSource<Character> apply(String s) throws Exception {
                        return Observable.create(new ObservableOnSubscribe<Character>() {
                            @Override
                            public void subscribe(ObservableEmitter<Character> emitter) throws Exception {
                                for (Character c : s.toCharArray()) {
                                    emitter.onNext(c);
                                }
                                emitter.onComplete();
                            }
                        });
                    }
                },
                new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        System.out.println("Disposable: " + s);
                    }
                }
        ).subscribe(new Observer<Character>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Character character) {
                System.out.println("onNext: " + character);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    public static void delayExample() {
        Observable.just(1, 2, 3, 4, 5)
                .delay(2, TimeUnit.SECONDS)
                .observeOn(Schedulers.from(Executors.newFixedThreadPool(1)))
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        System.out.println("onNext: " + integer);

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
