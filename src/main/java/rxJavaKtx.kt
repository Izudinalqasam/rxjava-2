import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import io.reactivex.schedulers.Schedulers

fun main() {
    composeOperatorExample()
}

fun composeOperatorExample(){
    Observable.just(1, 2, 3)
            .compose(toAnotherData())
            .subscribe({
                println(it)
            },{
                println(it)
            },{
                println("Complete")
            })
}


fun toAnotherData() : ObservableTransformer<Int, String>{
    return ObservableTransformer{
        it.map { number -> "$number kata" }
                .observeOn(Schedulers.trampoline())
    }
}


fun simpleObservable(){
    Observable.just(1,2,3)
            .map { it * it }
            .subscribe({
                println(it.toString())
            },{
                println("Gagal : $it")
            },{
                println("Completed")
            })
}
