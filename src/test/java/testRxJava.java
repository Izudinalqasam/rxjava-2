import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.TestScheduler;
import io.reactivex.subscribers.TestSubscriber;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.core.IsCollectionContaining.*;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class testRxJava {

    @Test
    public void tesRxJava2(){
        // test without Rx test
        List<String> letters = Arrays.asList("A","B","C","D","E");
        List<String> result = new ArrayList<>();
        CompositeDisposable compositeDisposable = new CompositeDisposable();

        Observable.fromIterable(letters)
                .zipWith(Observable.range(1, Integer.MAX_VALUE),
                        (string,index) -> index + "-" + string)
                .subscribe(result::add);

        assertThat(result, notNullValue());
        assertThat(result, hasItems("1-A", "2-B", "3-C", "4-D", "5-E"));

        //test with testObserver() to test asyncronously
        TestObserver<String> observer = new TestObserver<>();

        compositeDisposable.add(Observable.fromIterable(letters)
                .zipWith(Observable.range(1,Integer.MAX_VALUE)
                        ,(string,index) -> index + "-" + string)
                .subscribeWith(observer));

        observer.assertComplete();
        observer.assertNoErrors();
        observer.assertValueCount(5);
        assertThat(observer.values(), hasItems("1-A", "2-B", "3-C", "4-D", "5-E"));
        assertFalse(observer.isDisposed());

        compositeDisposable.clear();
        assertTrue(observer.isDisposed());
    }

    @Test
    public void testRxJava2Error(){
        List<String> letters = Arrays.asList("A", "B", "C", "D", "E");
        TestObserver<String> testObserver = new TestObserver<>();

        Observable.fromIterable(letters)
                .zipWith(Observable.range(1,Integer.MAX_VALUE),
                        (sting,index) -> index + "-" + sting)
                .concatWith(Observable.error(new RuntimeException("error in Observable")))
                .subscribe(testObserver);

        testObserver.assertError(RuntimeException.class);
        testObserver.assertNotComplete();
    }

    @Test
    public void testRxJava2TimeBased(){
        List<String> letters = Arrays.asList("A", "B", "C", "D", "E");
        TestScheduler scheduler = new TestScheduler();
        TestObserver<String> observer = new TestObserver<>();

        Observable<Long> tick = Observable.interval(1, TimeUnit.SECONDS,scheduler);

       Observable.fromIterable(letters)
               .zipWith(tick,(string,index) -> index + "-" + string)
               .subscribeOn(scheduler)
               .subscribe(observer);

       // to emulate the observer to observable emitted the item we use advanceTimeBy
        scheduler.advanceTimeBy(2,TimeUnit.SECONDS);


       observer.assertValueCount(2);
       observer.assertNoErrors();
       assertThat(observer.values(),hasItems("0-A","1-B"));
    }
}
