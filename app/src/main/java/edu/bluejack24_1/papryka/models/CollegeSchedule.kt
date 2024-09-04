package edu.bluejack24_1.papryka.models

import edu.bluejack24_1.papryka.utils.getDayOfWeek
import edu.bluejack24_1.papryka.utils.getShiftNumber


typealias CollegeSchedule = Map<String, List<CollegeDetail>>

fun processCollegeSchedule(response: CollegeSchedule): MutableList<Schedule> {
    val schedules = mutableListOf<Schedule>()

    response.values.forEach { detail ->
        detail.forEach {
            val endTime = when (it.Shift) {
                1 -> "09:00"
                2 -> "11:00"
                3 -> "13:00"
                4 -> "15:00"
                5 -> "17:00"
                6 -> "19:00"
                7 -> "21:00"
                else -> ""
            }

            val startTime = when (it.Shift) {
                1 -> "07:20"
                2 -> "09:20"
                3 -> "11:20"
                4 -> "13:20"
                5 -> "15:20"
                6 -> "17:20"
                7 -> "19:20"
                else -> ""
            }


            val schedule = Schedule(
                "${it.CourseCode} - ${it.CourseName}",
                "",
                getDayOfWeek("M/d/yyyy hh:mm:ss a", it.StartDate),
                .0F,
                "$startTime - $endTime",
                it.Room,
                "College",
                ""
            )

            schedule.ShiftCode = getShiftNumber(schedule.Shift)

            schedules.add(schedule)
        }
    }

    println(schedules)

    return schedules
}