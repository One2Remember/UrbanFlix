package com.example.myurbanflix;

import android.util.Log;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * Class is used as a firebase adapter which wraps around recyclerview.adapter and optimizes
 * recycler for user with google firebase
 * @param <VH>
 */
public abstract class FirestoreAdapter<VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> implements EventListener<QuerySnapshot> {

    /**
     * Used to label events in Logcat. Allows us to search for errors associated with the adapter.
     */
    private static final String TAG = "Firestore Adapter";

    /**
     * Holds the query given to the adapter in order to populate its data
     */
    private Query query;

    /**
     * Event listener
     */
    private ListenerRegistration registration;

    /**
     * List of document snapshots generated from query to database
     */
    private ArrayList<DocumentSnapshot> snapshots = new ArrayList<>();

    /**
     * Constructor that sets the query for the adapter
     * @param query
     */
    public FirestoreAdapter(Query query) {
        this.query = query;
    }

    /**
     * Add listener to query
     */
    public void startListening() {
        if (query != null && registration == null) {
            registration = query.addSnapshotListener(this);
        }
    }

    /**
     * Remove listener from query
     */
    public void stopListening() {
        if (registration != null) {
            registration.remove();
            registration = null;
        }
        snapshots.clear();
        notifyDataSetChanged();
    }

    /**
     * Gets the size of the document snapshot ArrayList
     * @return
     */
    @Override
    public int getItemCount() {
        return snapshots.size();
    }

    /**
     * Return document snapshot at given index
     * @param index
     * @return
     */
    protected DocumentSnapshot getSnapshot(int index) {
        return snapshots.get(index);
    }

    /**
     * Template function to do something when modifying the document snapshots returns an error.
     * Override when new instance of adapter is instantiated
     * @param e
     */
    protected void onError(FirebaseFirestoreException e) {
        Log.d("Generic", "This should not be called");
    }

    /**
     * Template function to do something when data on the document snapshot is modified.
     * Override when new instance of Adapter is instantiated.
     */
    protected void onDataChanged() {
    }

    /**
     * Updates document snapshot list when a document has been added and updates indices
     * @param change
     */
    protected void onDocumentAdded(DocumentChange change) {
        snapshots.add(change.getNewIndex(), change.getDocument());
        notifyItemInserted(change.getNewIndex());
    }

    /**
     * Updates document snapshot when it has been modified, indices remain unchanged
     * @param change
     */
    protected void onDocumentModified(DocumentChange change) {
        if (change.getOldIndex() == change.getNewIndex()) {
            // Item changed but remained in same position
            snapshots.set(change.getOldIndex(), change.getDocument());
            notifyItemChanged(change.getOldIndex());
        } else {
            // Item changed and changed position
            snapshots.remove(change.getOldIndex());
            snapshots.add(change.getNewIndex(), change.getDocument());
            notifyItemMoved(change.getOldIndex(), change.getNewIndex());
        }
    }

    /**
     * Updates document snapshot list when a document is removed and updates indices
     * @param change
     */
    protected void onDocumentRemoved(DocumentChange change) {
        snapshots.remove(change.getOldIndex());
        notifyItemRemoved(change.getOldIndex());
    }

    /**
     * Puts the onDocumentXXX functions together into an onEvent handler
     * @param documentSnapshots
     * @param e
     */
    @Override
    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
        // Handle errors
        if (e != null) {
            Log.w(TAG, "onEvent:error", e);
            return;
        }
        // Dispatch event
        for (DocumentChange change : documentSnapshots.getDocumentChanges()) {
            // Snapshot of the changed document
            DocumentSnapshot snapshot = change.getDocument();
            switch (change.getType()) {
                case ADDED:
                    onDocumentAdded(change);
                    break;
                case MODIFIED:
                    onDocumentModified(change);
                    break;
                case REMOVED:
                    onDocumentRemoved(change);
                    break;
            }
        }
        onDataChanged();
    }
}
