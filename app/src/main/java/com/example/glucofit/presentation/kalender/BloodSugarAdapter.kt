import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.glucofit.R
import com.example.glucofit.presentation.kalender.KalenderActivity
import java.text.SimpleDateFormat
import java.util.*

class BloodSugarAdapter(private val bloodSugarList: List<KalenderActivity.BloodSugarData>) :
    RecyclerView.Adapter<BloodSugarAdapter.BloodSugarViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BloodSugarViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_blood_sugar, parent, false)
        return BloodSugarViewHolder(view)
    }

    override fun onBindViewHolder(holder: BloodSugarViewHolder, position: Int) {
        val bloodSugarData = bloodSugarList[position]
        holder.bind(bloodSugarData)
    }

    override fun getItemCount(): Int = bloodSugarList.size

    inner class BloodSugarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        private val tvBloodSugar: TextView = itemView.findViewById(R.id.tvBloodSugar)
        private val tvResult: TextView = itemView.findViewById(R.id.tvResult)

        fun bind(data: KalenderActivity.BloodSugarData) {
            tvBloodSugar.text = "Kadar Gula Darah : ${data.bloodSugar ?: "N/A"}"
            tvResult.text = "Hasil : ${data.result ?: "N/A"}"
            tvDate.text = formatDate(data.date ?: "0000-00-00")
        }

        private fun formatDate(dateString: String): String {
            return try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale("id", "ID"))
                val outputFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
                val date = inputFormat.parse(dateString)
                date?.let { outputFormat.format(it) } ?: dateString
            } catch (e: Exception) {
                dateString
            }
        }
    }
}
