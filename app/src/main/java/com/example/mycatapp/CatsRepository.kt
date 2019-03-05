package com.example.mycatapp

import io.reactivex.Single
import io.reactivex.functions.Function

class CatsRepository(
    baseUrl: String,
    isDebugEnabled: Boolean,
    apiKey: String
) : Repository(baseUrl, isDebugEnabled, apiKey) {

    private val catsDataSource: CatsDataSource = CatsDataSource(retrofit)

    // a class to wrap around the response to make things easier later
    inner class Result(val netCats: List<NetCat>? = null, val errorMessage: String? = null) {
        fun hasCats(): Boolean {
            return netCats != null && !netCats.isEmpty()
        }
        fun hasError(): Boolean {
            return errorMessage != null
        }
    }

    fun getNumberOfRandomCats(limit: Int, category_ids: Int?): Single<Result> {
        return catsDataSource
            .getNumberOfRandomCats(limit, category_ids) // 반환값  Single<List<NetCat>>
//            .map { netCats: List<NetCat> -> Result(netCats = netCats) }  // Single<List<NetCat>>.map 이거라고 보면댐.
            .map(object : Function<List<NetCat>,Result> {
                //map. 결론적으로 api요청으로 받아온 값을, 우리가 쉽게 쓸수있는 클래스로 매핑해준다 생각하자. 여기선 Result 이다.
                override fun apply(intupValue: List<NetCat>): Result {
                    return Result(intupValue)
                }
            })
            .onErrorReturn { t: Throwable -> Result(errorMessage = t.message) }
    }

}