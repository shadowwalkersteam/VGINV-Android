package com.techno.vginv.features.demo.custom.holder.holders.messages;

import android.view.View;

import com.stfalcon.chatkit.messages.MessageHolders;
import com.techno.vginv.data.model.Message;

public class CustomOutcomingTextMessageViewHolder
        extends MessageHolders.OutcomingTextMessageViewHolder<Message> {

    public CustomOutcomingTextMessageViewHolder(View itemView, Object payload) {
        super(itemView, payload);
    }

    @Override
    public void onBind(Message message) {
        super.onBind(message);

        time.setText(message.getStatus() + " " + time.getText());
    }
}
