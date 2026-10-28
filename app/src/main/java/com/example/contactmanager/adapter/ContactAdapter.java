package com.example.contactmanager.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.contactmanager.R;
import com.example.contactmanager.model.Contact;

import java.util.List;

/**
 * Adapter quản lý việc hiển thị danh sách liên hệ trên RecyclerView.
 */
public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    public interface OnContactClickListener {
        void onContactClick(Contact contact, int position);
    }

    private final Context context;
    private final List<Contact> contactList;
    private final OnContactClickListener listener;

    private final int[] avatarColors = {
            R.color.avatar_an,
            R.color.avatar_binh,
            R.color.avatar_chau,
            R.color.avatar_other1,
            R.color.avatar_other2,
            R.color.avatar_other3
    };

    public ContactAdapter(Context context, List<Contact> contactList, OnContactClickListener listener) {
        this.context = context;
        this.contactList = contactList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_contact, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contact contact = contactList.get(position);

        // Họ tên & Số điện thoại định dạng chuẩn
        holder.tvContactName.setText(contact.getName());
        holder.tvContactPhone.setText(contact.getFormattedPhone());

        // Nhãn nhóm
        holder.tvContactGroup.setText(contact.getGroup());

        // Ký tự Avatar
        String letter = contact.getAvatarLetter();
        holder.tvAvatar.setText(letter);

        // Màu nền Avatar: Pastel sang trọng (An: Teal #0D9488, Bình: Orange #EA580C, Châu: Indigo #4F46E5)
        int colorRes;
        switch (letter) {
            case "A":
                colorRes = R.color.avatar_an;
                break;
            case "B":
                colorRes = R.color.avatar_binh;
                break;
            case "C":
                colorRes = R.color.avatar_chau;
                break;
            default:
                int index = Math.abs(contact.getName().hashCode()) % avatarColors.length;
                colorRes = avatarColors[index];
                break;
        }

        Drawable bgDrawable = ContextCompat.getDrawable(context, R.drawable.bg_avatar_circle);
        if (bgDrawable != null) {
            Drawable wrapped = DrawableCompat.wrap(bgDrawable.mutate());
            DrawableCompat.setTint(wrapped, ContextCompat.getColor(context, colorRes));
            holder.tvAvatar.setBackground(wrapped);
        }

        // Ngôi sao yêu thích (Amber Gold #D97706)
        if (contact.isFavorite()) {
            holder.imgFavoriteStar.setVisibility(View.VISIBLE);
        } else {
            holder.imgFavoriteStar.setVisibility(View.GONE);
        }

        // Bắt sự kiện click vào dòng liên hệ
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onContactClick(contact, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return contactList != null ? contactList.size() : 0;
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView tvAvatar;
        TextView tvContactName;
        TextView tvContactPhone;
        TextView tvContactGroup;
        ImageView imgFavoriteStar;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAvatar = itemView.findViewById(R.id.tvAvatar);
            tvContactName = itemView.findViewById(R.id.tvContactName);
            tvContactPhone = itemView.findViewById(R.id.tvContactPhone);
            tvContactGroup = itemView.findViewById(R.id.tvContactGroup);
            imgFavoriteStar = itemView.findViewById(R.id.imgFavoriteStar);
        }
    }
}
