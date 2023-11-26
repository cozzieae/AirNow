package edu.appstate.cs.moments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MomentsListViewModel() : ViewModel() {

//    private val places = listOf(
//        "Anne Belk Hall",
//        "Appalachian Hall",
//        "Belk Library",
//        "Broyhill Music Center",
//        "Edwin Duncan Hall",
//        "Garwood Hall",
//        "Peacock Hall",
//        "Rankin Science West",
//        "Sanford Hall",
//        "Plemmons Student Center"
//    )
//
//    private val descriptions = listOf(
//        "Studying at ",
//        "Relaxing at ",
//        "Reading a book at ",
//        "Meeting friends at ",
//        "Reading a book at ",
//        "Watching a video at ",
//        "Drinking a coffee at ",
//        "Grabbing a snack at ",
//        "Trying not to fall asleep at ",
//        "Going to class at "
//    )
//
//    private fun randomPlace() = places.random()
//
//    private fun randomDescription(place: String) = descriptions.random() + place
//
//    val moments = mutableListOf<Moment>()
//
//    init {
//        for (i in 0 until 100) {
//            val place = randomPlace()
//            val moment = Moment(
//                place,
//                randomDescription(place)
//            )
//            moments += moment
//        }
//    }


    private val momentsRepository = MomentsRepository.get()

    private val _moments: MutableStateFlow<List<Moment>> = MutableStateFlow(emptyList())

    val moments: StateFlow<List<Moment>>
        get() = _moments.asStateFlow()

    init {
        viewModelScope.launch {
            momentsRepository.getMoments().collect {
                _moments.value = it
            }
        }
    }

    suspend fun addMoment(moment: Moment) {
        momentsRepository.addMoment(moment)
    }

    suspend fun deleteMoment(moment: Moment) {
        momentsRepository.deleteMoment(moment)
    }
}