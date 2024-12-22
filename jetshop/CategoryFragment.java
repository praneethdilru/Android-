package com.theempires.jetshop;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.theempires.jetshop.adapter.itemAdapter;
import com.theempires.jetshop.model.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.

 * create an instance of this fragment.
 */
public class CategoryFragment extends Fragment {

    private FirebaseFirestore firestore;
    private FirebaseStorage storage;

    private CollectionReference itemsCollection;

    private ArrayList<Item> items;
    private List<Item> filteredItems;
    private itemAdapter itemAdapter;
    public CategoryFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_category, container, false);


        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        items = new ArrayList<>();
        filteredItems = new ArrayList<>();

        RecyclerView itemView = view.findViewById(R.id.itemView);
        EditText searchEditText = view.findViewById(R.id.searchView);


        itemAdapter = new itemAdapter((ArrayList<Item>) filteredItems, CategoryFragment.this);
        itemAdapter.setOnItemClickListener(new itemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(getActivity(), OderActivity.class);
                intent.putExtra("Items", filteredItems.get(position));
                startActivity(intent);

            }
        });



        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);

        itemView.setLayoutManager(layoutManager);
        itemView.setAdapter(itemAdapter);

        fetchItems();


        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filterItems(charSequence.toString().toLowerCase(Locale.getDefault()));
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        return view;

    }




    private void fetchItems() {
        firestore.collection("Items").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                items.clear();
                for (QueryDocumentSnapshot snapshot : task.getResult()) {
                    Item item = snapshot.toObject(Item.class);
                    items.add(item);
                }
                filterItems("");
            } else {

            }
        });
    }

    private void filterItems(String query) {
        filteredItems.clear();


        for (Item item : items) {
            if (item.getName() != null && item.getName().toLowerCase(Locale.getDefault()).contains(query)) {
                filteredItems.add(item);
            }
        }

        itemAdapter.notifyDataSetChanged();
    }

    private Item findItemById(String itemId) {
        for (Item item : items) {
            if (item.getId().equals(itemId)) {
                return item;
            }
        }
        return null;
    }
}



