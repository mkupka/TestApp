package com.adt.testApp.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.adt.testApp.R
import com.adt.testApp.databinding.ListItemCharacterActorBinding
import com.adt.testApp.ui.main.rest.models.CharacterActor
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val ITEM_VIEW_TYPE_NO_ITEMS = 0
private const val ITEM_VIEW_TYPE_ITEM = 1

class CharacterActorListAdapter(val clickListener: CharacterActorListener) : ListAdapter<DataItem, RecyclerView.ViewHolder>(CharacterActorDiffCallback()) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    fun addHeaderAndSubmitList(list: List<CharacterActor>?) {
        adapterScope.launch {

            val items = when (list) {
                null -> listOf(DataItem.Header)
                else -> list.map { DataItem.CharacterActorItem(it) }
            }

            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is CharacterActorListItemViewHolder -> {
                val characterActorItem = getItem(position) as DataItem.CharacterActorItem
                holder.bind(clickListener, characterActorItem.characterActor)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_NO_ITEMS -> EmptyCharacterActorListViewHolder.from(parent)
            ITEM_VIEW_TYPE_ITEM -> CharacterActorListItemViewHolder.from(parent)
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    class EmptyCharacterActorListViewHolder(view: View): RecyclerView.ViewHolder(view) {
        companion object {
            fun from(parent: ViewGroup): EmptyCharacterActorListViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.list_item_character_actor, parent, false)
                return EmptyCharacterActorListViewHolder(view)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is DataItem.Header -> ITEM_VIEW_TYPE_NO_ITEMS
            is DataItem.CharacterActorItem -> ITEM_VIEW_TYPE_ITEM
        }
    }

    class CharacterActorListItemViewHolder private constructor(val binding: ListItemCharacterActorBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(clickListener: CharacterActorListener, item: CharacterActor) {
            binding.characterActor = item
            binding.clickListener = clickListener
            //
            // binding.
            Picasso.get()
                .load(item.image)
                .into(binding.imageActor)
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): CharacterActorListItemViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemCharacterActorBinding.inflate(layoutInflater, parent, false)
                return CharacterActorListItemViewHolder(binding)
            }
        }
    }
}

/**
 * Callback for calculating the diff between two non-null items in a list.
 *
 * Used by ListAdapter to calculate the minimum number of changes between and old list and a new
 * list that's been passed to `submitList`.
 */
class CharacterActorDiffCallback : DiffUtil.ItemCallback<DataItem>() {
    override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem == newItem
    }
}

class CharacterActorListener(val clickListener: (characterActor: CharacterActor) -> Unit) {
    fun onClick(characterActor: CharacterActor) = clickListener(characterActor)
}

sealed class DataItem {
    data class CharacterActorItem(val characterActor: CharacterActor): DataItem() {
        override val id: Int = characterActor.id
    }

    object Header: DataItem() {
        override val id: Int = 0
    }

    abstract val id: Int
}