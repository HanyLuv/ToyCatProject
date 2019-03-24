package com.example.mycatapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.ReplaySubject
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val serverUrl = "https://api.thecatapi.com/v1/"
    private lateinit var apiKey: String

    private val compositeDisposableOnPause = CompositeDisposable()
    private var latestCatCall: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        apiKey = getString(R.string.cat_api_key)

        button.setOnClickListener {
            getSomeCats()
        }
    }

    private fun getSomeCats() {
        val catsRepository = CatsRepository(serverUrl, BuildConfig.DEBUG, apiKey)

        latestCatCall?.dispose() //이게 만약에 실행중이면 스톱하는거야.
        //Schedulers.io() - 동기 I/O를 별도로 처리시켜 비동기 효율을 얻기 위한 스케줄러.
        //자체적인 스레드 풀 CachedThreadPool을 사용합니다. API 호출 등 네트워크를 사용한 호출 시 사용됩니다.
        
        //subscribeOn 는 Observable이 동작하는 스케쥴러를 다른 스케쥴러로 지정하여 동작을 변경함.
        //Reactive-Stream에서 사용할 기본 스케줄러이며, observeOn으로 스케줄러 교체하기 전까지 기본으로 사용한다.
        latestCatCall = catsRepository.getNumberOfRandomCats(10, null).subscribeOn(Schedulers.io())
                .doOnSubscribe {
                    //RxJava를 사용할때 스트림을 취소해 주지 않으면 계속 돌기때문에 위험하다.
                    //이를 해결하기위해 ArrayStream에 만들어진 코드를 넣어두고 한번에 취소하는 방법을 사용해야한다.
                    compositeDisposableOnPause.add(it)
                }
                .observeOn(AndroidSchedulers.mainThread()) //결과를 받을 Thread.
            //RxJava는 편의를 위해 사용하지 않는 구성이 누락된 인터페이스를 제공한다.
                .subscribe { result -> //구독 받은거
                    when {
                        result.hasError() -> result.errorMessage?.let {
                            Toast.makeText(this@MainActivity, "Error getting cats$it", Toast.LENGTH_SHORT).show()
                        }
                            ?: run {
                                Toast.makeText(this@MainActivity, "Null error", Toast.LENGTH_SHORT).show()
                            }
                        result.hasCats() -> result.netCats?.let {
                            Toast.makeText(this@MainActivity, "Cats received!", Toast.LENGTH_SHORT).show()
                        }
                            ?: run {
                                Toast.makeText(this@MainActivity, "Null list of cats", Toast.LENGTH_SHORT).show()
                            }
                        else -> Toast.makeText(this@MainActivity, "No cats available :(", Toast.LENGTH_SHORT).show()
                    }
                }
    }

    private fun clearAllJobsOnPause() {
        compositeDisposableOnPause.clear()
    }

    override fun onPause() {
        clearAllJobsOnPause()
        super.onPause()
    }
}