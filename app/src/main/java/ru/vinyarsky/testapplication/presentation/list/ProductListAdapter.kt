package ru.vinyarsky.testapplication.presentation.list

import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import ru.vinyarsky.testapplication.R
import ru.vinyarsky.testapplication.domain.models.Product
import java.util.zip.Inflater

class ProductListAdapter(
    private val data: List<Product>,
    private val onClick: (productId: Int) -> Unit
) : RecyclerView.Adapter<ProductListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.fragment_product_list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int = data.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val content: LinearLayout = view.findViewById(R.id.product_list_item_content)
        private val name: TextView = view.findViewById(R.id.product_list_item_name)
        private val price: TextView = view.findViewById(R.id.product_list_item_price)
        private val rating: RatingBar = view.findViewById(R.id.product_list_item_rating)

        fun bind(product: Product) {
            name.text = product.name
            if (product.price != null) {
                // Of course, a proper way would be to have a separate model for a view
                price.text = content.resources.getString(
                    R.string.readable_price,
                    product.price,
                    product.currency.name
                )
            } else {
                price.text = content.resources.getString(R.string.price_not_available)
            }
            rating.rating = product.rating
            content.setOnClickListener { onClick(product.id) }
        }
    }
}