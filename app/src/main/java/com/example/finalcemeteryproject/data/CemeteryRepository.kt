package com.example.finalcemeteryproject.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.finalcemeteryproject.network.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Response
import timber.log.Timber

class CemeteryRepository(private val cemeteryDao: CemeteryDao) {


    val retrofit = ServiceBuilder.networkAccessor

    fun getAllCemeteries() : LiveData<List<Cemetery>> {
        return cemeteryDao.getAllCemeteries()
    }


    suspend fun insertCemetery(cemetery: Cemetery){
        cemeteryDao.insertCemetery(cemetery)
    }

    suspend fun deleteGrave(rowid: Int){
        cemeteryDao.deleteGrave(rowid)
    }

    suspend fun insertGrave(grave: Grave){
        cemeteryDao.insertGrave(grave)
    }

    fun getGraveWithRowId(rowid: Int): LiveData<Grave>{
        return cemeteryDao.getGraveWithRowid(rowid)
    }

    fun getCemeteryWithId(cemeteryId: Int): LiveData<Cemetery>{
        return cemeteryDao.getCemeteryWithId(cemeteryId)
    }

    fun getGravesWithCemeteryId(cemeteryId: Int): LiveData<List<Grave>>{
        return cemeteryDao.getGravesWithCemeteryId(cemeteryId)
    }

     suspend fun getMaxCemeteryRowNum() : Int? {
       return  cemeteryDao.getMaxCemeteryRowNum()
    }


    //network calls
//
//    fun sendNewCemeteryToNetwork(cemetery: Cemetery, onResult: (Cemetery?) -> Unit){
//        val retrofit = ServiceBuilder.buildService(NetworkApi::class.java)
//        retrofit.sendNewCemeteryToNetwork(
//            cemId = cemetery.cemeteryRowId.toString(),
//            cemName = cemetery.cemeteryName,
//            location = cemetery.cemeteryLocation,
//            county = cemetery.cemeteryCounty,
//            township = cemetery.township,
//            range = cemetery.range,
//            spot = cemetery.spot,
//            yearFounded = cemetery.firstYear,
//            section = cemetery.section,
//            state = cemetery.cemeteryState).enqueue(
//
//            object : retrofit2.Callback<Cemetery> {
//                override fun onFailure(call: Call<Cemetery>, t: Throwable) {
//                    onResult(null)
//                }
//                override fun onResponse(call: Call<Cemetery>, response: Response<Cemetery>) {
//                    val addedCemetery = response.body()
//                    onResult(addedCemetery)
//                }
//            }
//        )
//    }

//
//    fun sendNewGraveToNetwork(grave: Grave, onResult: (Grave?) -> Unit){
//        val retrofit = ServiceBuilder.buildService(NetworkApi::class.java)
//        retrofit.sendNewGraveToNetwork(
//            id = grave.graveRowId,
//            cemeteryId = grave.cemeteryId,
//            firstName = grave.firstName,
//            lastName = grave.lastName,
//            bornDate = grave.birthDate,
//            deathDate = grave.deathDate,
//            marriageYear = grave.marriageYear,
//            comment = grave.comment,
//            graveNum = grave.graveNumber
//        ).enqueue(
//
//            object : retrofit2.Callback<Grave> {
//                override fun onFailure(call: Call<Grave>, t: Throwable) {
//                    onResult(null)
//                }
//                override fun onResponse(call: Call<Grave>, response: Response<Grave>) {
//                    val addedGrave = response.body()
//                    onResult(addedGrave)
//                }
//            }
//        )
//    }



    // new ways this may not work probably use the old way and in view model you can use the callback to set a toast message that is live data dependant

//    suspend fun newWaySendCemeteryToNetwork(cemetery: Cemetery){ //need to send this as a json object to dad
//        retrofit.sendNewCemeteryToNetworkNewWay(
//            cemId = cemetery.cemeteryRowId.toString(),
//            cemName = cemetery.cemeteryName,
//            location = cemetery.cemeteryLocation,
//            county = cemetery.cemeteryCounty,
//            township = cemetery.township,
//            range = cemetery.range,
//            spot = cemetery.spot,
//            yearFounded = cemetery.firstYear,
//            section = cemetery.section,
//            state = cemetery.cemeteryState
//        )
//    }

     fun newWaySendCemeteryToNetwork(cemetery: Cemetery, onResult: (Cemetery?) -> Unit){ //need to send this as a json object to dad
        retrofit.sendNewCemeteryToNetworkNewWay(
            cemId = cemetery.cemeteryRowId.toString(),
            cemName = cemetery.cemeteryName,
            location = cemetery.cemeteryLocation,
            county = cemetery.cemeteryCounty,
            township = cemetery.township,
            range = cemetery.range,
            spot = cemetery.spot,
            yearFounded = cemetery.firstYear,
            state = cemetery.cemeteryState
        ).enqueue(
            object : retrofit2.Callback<Cemetery> {
                override fun onFailure(call: Call<Cemetery>, t: Throwable) {
                    onResult(null)
                }
                override fun onResponse(call: Call<Cemetery>, response: Response<Cemetery>) {
                    val addedCemetery = response.body()
                    onResult(addedCemetery)
                }
            }
        )
    }



    fun refreshCemeteryList(onResult: (NetworkCemeteryContainer?) -> Unit) {

        val cemeteryNetworkList = retrofit.getCemeteriesFromNetworkNewWay()

        cemeteryNetworkList.enqueue(
            object : retrofit2.Callback<NetworkCemeteryContainer> {
                override fun onFailure(call: Call<NetworkCemeteryContainer>, t: Throwable) {
                    onResult(null)
                }

                override fun onResponse(
                    call: Call<NetworkCemeteryContainer>,
                    response: Response<NetworkCemeteryContainer>
                ) {
                    val networkCemeteryContainer = response.body()
                    onResult(networkCemeteryContainer)

                }
            }
        )
    }



    suspend fun insertNetworkCems(cemetery: NetworkCemeteryContainer){
        Timber.d("inserting")
        Log.i("Doing work in view model", "work")

        cemeteryDao.insertCemeteryNetworkList(*cemetery.asDatabaseModel())
    }






}
