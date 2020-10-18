package com.example.fa.DeliveryFoodPanel;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fa.R;
import com.example.fa.ReusableCode.ReusableCodeForAll;
import com.example.fa.SendNotification.APIService;
import com.example.fa.SendNotification.Client;
import com.example.fa.SendNotification.Data;
import com.example.fa.SendNotification.MyResponse;
import com.example.fa.SendNotification.NotificationSender;

import com.example.fa.SendNotification.Client;
import com.example.fa.SendNotification.Data;
import com.example.fa.SendNotification.MyResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeliveryPendingOrderFragmentAdapter extends RecyclerView.Adapter<DeliveryPendingOrderFragmentAdapter.ViewHolder> {

    private static final String TAG = "DeliveryPendingOrder";

    private Context context;
    private List<DeliveryShipOrders1> deliveryShipOrders1list;
    private APIService apiService;
    String chefid;
    private ViewHolder holder;
    private int position;
    private static final String DELIVERY_ID = "FiskLkwpt9ZYgm19yZB8ldmrqD22";


    public DeliveryPendingOrderFragmentAdapter(Context c, List<DeliveryShipOrders1> deliveryShipOrders1l) {
        this.deliveryShipOrders1list = deliveryShipOrders1l;
        this.context = c;
    }

    @NonNull
    @Override
    public DeliveryPendingOrderFragmentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.delivery_pendingorders, parent, false);
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        return new DeliveryPendingOrderFragmentAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull DeliveryPendingOrderFragmentAdapter.ViewHolder holder, int position) {
        this.holder = holder;
        this.position = position;

        Log.i(TAG, MessageFormat.format("Position: {0}", position));

        final DeliveryShipOrders1 deliveryShipOrders1 = deliveryShipOrders1list.get(position);

        if (deliveryShipOrders1 == null) {
            return;
        }

        Log.println(Log.INFO, "Delivery Position", position + "");
        holder.Address.setText(deliveryShipOrders1.getAddress());
        holder.mobilenumber.setText("+91" + deliveryShipOrders1.getMobileNumber());
        holder.grandtotalprice.setText("Grand Total: ₹ " + deliveryShipOrders1.getGrandTotalPrice());
        final String randomUID = deliveryShipOrders1.getRandomUID();
        holder.Vieworder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, DeliveryPendingOrderView.class);
                intent.putExtra("Random", randomUID);
                context.startActivity(intent);
            }
        });

        holder.Accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("DeliveryShipOrders").child(DELIVERY_ID).child(randomUID).child("Dishes");
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            DeliveryShipOrders deliveryShipOrderss = snapshot.getValue(DeliveryShipOrders.class);
                            HashMap<String, String> hashMap = new HashMap<>();
                            String dishid = deliveryShipOrderss.getDishId();
                            chefid = deliveryShipOrderss.getChefId();
                            hashMap.put("ChefId", deliveryShipOrderss.getChefId());
                            hashMap.put("DishId", deliveryShipOrderss.getDishId());
                            hashMap.put("DishName", deliveryShipOrderss.getDishName());
                            hashMap.put("DishPrice", deliveryShipOrderss.getDishPrice());
                            hashMap.put("DishQuantity", deliveryShipOrderss.getDishQuantity());
                            hashMap.put("RandomUID", deliveryShipOrderss.getRandomUID());
                            hashMap.put("TotalPrice", deliveryShipOrderss.getTotalPrice());
                            hashMap.put("UserId", deliveryShipOrderss.getUserId());
                            FirebaseDatabase.getInstance().getReference("DeliveryShipFinalOrders").child(DELIVERY_ID).child(randomUID).child("Dishes").child(dishid).setValue(hashMap);

                        }

                        DatabaseReference data = FirebaseDatabase.getInstance().getReference("DeliveryShipOrders").child(DELIVERY_ID).child(randomUID).child("OtherInformation");
                        data.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                DeliveryShipOrders1 deliveryShipOrders11 = dataSnapshot.getValue(DeliveryShipOrders1.class);
                                HashMap<String, String> hashMap1 = new HashMap<>();
                                hashMap1.put("Address", deliveryShipOrders11.getAddress());
                                hashMap1.put("ChefId", deliveryShipOrders11.getChefId());
                                hashMap1.put("ChefName", deliveryShipOrders11.getChefName());
                                hashMap1.put("GrandTotalPrice", deliveryShipOrders11.getGrandTotalPrice());
                                hashMap1.put("MobileNumber", deliveryShipOrders11.getMobileNumber());
                                hashMap1.put("Name", deliveryShipOrders11.getName());
                                hashMap1.put("RandomUID", randomUID);
                                hashMap1.put("UserId", deliveryShipOrders11.getUserId());
                                FirebaseDatabase.getInstance().getReference("DeliveryShipFinalOrders").child(DELIVERY_ID).child(randomUID).child("OtherInformation").setValue(hashMap1).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        FirebaseDatabase.getInstance().getReference("DeliveryShipOrders").child(DELIVERY_ID).child(randomUID).child("Dishes").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                FirebaseDatabase.getInstance().getReference("DeliveryShipOrders").child(DELIVERY_ID).child(randomUID).child("OtherInformation").removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        FirebaseDatabase.getInstance().getReference().child("Tokens").child(chefid).child("token").addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                String usertoken = dataSnapshot.getValue(String.class);
                                                                sendNotifications(usertoken, "Order Accepted", "Your Order has been Accepted by the Delivery person", "AcceptOrder");
                                                                ReusableCodeForAll.ShowAlert(context, "", "Now you can check orders which are to be shipped");

                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                            }
                                                        });
                                                    }
                                                }).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        FirebaseDatabase.getInstance().getReference("ChefFinalOrders").child(chefid).child(randomUID).child("Dishes").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                FirebaseDatabase.getInstance().getReference("ChefFinalOrders").child(chefid).child(randomUID).child("OtherInformation").removeValue();
                                                            }
                                                        });
                                                    }
                                                });

                                            }
                                        });
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        holder.Reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("DeliveryShipOrders").child(DELIVERY_ID).child(randomUID).child("Dishes");
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            DeliveryShipOrders deliveryShipOrders = dataSnapshot1.getValue(DeliveryShipOrders.class);
                            chefid = deliveryShipOrders.getChefId();
                        }

                        FirebaseDatabase.getInstance().getReference().child("Tokens").child(chefid).child("token").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String usertoken = dataSnapshot.getValue(String.class);
                                sendNotifications(usertoken, "Order Rejected", "Your Order has been Rejected by the Delivery person", "RejectOrder");
                                FirebaseDatabase.getInstance().getReference("DeliveryShipOrders").child(DELIVERY_ID).child(randomUID).child("Dishes").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        FirebaseDatabase.getInstance().getReference("DeliveryShipOrders").child(DELIVERY_ID).child(randomUID).child("OtherInformation").removeValue();
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

    }


    private void sendNotifications(String usertoken, String title, String message, String order) {

        Data data = new Data(title, message, order);
        NotificationSender sender = new NotificationSender(data, usertoken);
        apiService.sendNotification(sender).enqueue(new Callback<MyResponse>() {
            @Override
            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                if (response.code() == 200) {
                    if (response.body().success != 1) {
                        Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<MyResponse> call, Throwable t) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return deliveryShipOrders1list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView Address, grandtotalprice, mobilenumber;
        Button Vieworder, Accept, Reject;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Address = itemView.findViewById(R.id.ad1);
            mobilenumber = itemView.findViewById(R.id.MB1);
            grandtotalprice = itemView.findViewById(R.id.TP1);
            Vieworder = itemView.findViewById(R.id.view1);
            Accept = itemView.findViewById(R.id.accept1);
            Reject = itemView.findViewById(R.id.reject1);
        }
    }
}
