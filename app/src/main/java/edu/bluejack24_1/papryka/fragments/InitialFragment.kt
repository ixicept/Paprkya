package edu.bluejack24_1.papryka.fragments

class InitialFragment : ScheduleBaseFragment() {
    override fun getInitials() {
        val etInitial = vBinding.etInitial
        val parts = etInitial.text.toString().trim().split(";")
        initials.addAll(parts)
    }

    companion object {
        fun newInstance(date: String, day: String, shift: String, midCode: String): InitialFragment {
            val fragment = InitialFragment()
            fragment.arguments = createArguments(date, day, shift, midCode)
            return fragment
        }
    }
}