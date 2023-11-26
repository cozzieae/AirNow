package edu.appstate.cs.moments

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

// NOTE: We want to use Strings here, not string resources, since in the
// future we will be allowing users of our app to enter these themselves.
// At that point, it would be impossible to use resources for this.
@Entity
data class Moment(
    @PrimaryKey val id: UUID,
    var title: String,
    var description: String,
    var timestamp: Date = Date()
)