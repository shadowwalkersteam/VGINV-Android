/*******************************************************************************
 * Copyright 2016 stfalcon.com
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package com.stfalcon.chatkit.messages;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import androidx.core.view.ViewCompat;
import androidx.legacy.widget.Space;

import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.stfalcon.chatkit.R;

import java.lang.reflect.Field;

/**
 * Component for input outcoming messages
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class MessageInput extends RelativeLayout
        implements View.OnClickListener, TextWatcher, View.OnFocusChangeListener {

    protected EditText messageInput;
    protected ImageButton messageSendButton;
    protected ImageButton voiceMessageButton;
    protected ImageButton attachmentButton;
    protected Space sendButtonSpace, attachmentButtonSpace;

    protected ImageView recordClose;
    protected ImageView recordTick;
    protected Chronometer recordingTime;

    private CharSequence input;
    private InputListener inputListener;
    private VoiceRecordingListener voiceRecordingListener;
    private AttachmentsListener attachmentsListener;
    private boolean isTyping;
    private TypingListener typingListener;
    private int delayTypingStatusMillis;
    private Runnable typingTimerRunnable = new Runnable() {
        @Override
        public void run() {
            if (isTyping) {
                isTyping = false;
                if (typingListener != null) typingListener.onStopTyping();
            }
        }
    };
    private boolean isRecordingClicked = false;
    private boolean lastFocus;

    private AttributeSet attributeSet;
    private Context context;
    private int count = 0;

    public MessageInput(Context context) {
        super(context);
        init(context);
    }

    public MessageInput(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MessageInput(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    /**
     * Sets callback for 'submit' button.
     *
     * @param inputListener input callback
     */
    public void setInputListener(InputListener inputListener) {
        this.inputListener = inputListener;
    }

    public void setVoiceRecordingListener(VoiceRecordingListener inputListener) {
        this.voiceRecordingListener = inputListener;
    }

    /**
     * Sets callback for 'add' button.
     *
     * @param attachmentsListener input callback
     */
    public void setAttachmentsListener(AttachmentsListener attachmentsListener) {
        this.attachmentsListener = attachmentsListener;
    }

    /**
     * Returns EditText for messages input
     *
     * @return EditText
     */
    public EditText getInputEditText() {
        return messageInput;
    }

    /**
     * Returns `submit` button
     *
     * @return ImageButton
     */
    public ImageButton getButton() {
        return messageSendButton;
    }

    public ImageButton getVoiceMessageButton() {
        return voiceMessageButton;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.messageSendButton) {
            boolean isSubmitted = onSubmit();
            if (isSubmitted) {
                messageInput.setText("");
            }
            removeCallbacks(typingTimerRunnable);
            post(typingTimerRunnable);
        } else if (id == R.id.attachmentButton) {
            onAddAttachments();
        } else if (id == R.id.voiceMessgage) {
            updateUI(true);
            onRecording(false);
        } else if (id == R.id.voiceTick) {
            updateUI(false);
            onRecording(false);
        } else if (id == R.id.voiceClose) {
            updateUI(false);
            onRecording(true);
        }
    }


    private void updateUI(boolean isShow) {
        if (isShow) {
            recordClose.setVisibility(VISIBLE);
            recordTick.setVisibility(VISIBLE);
            recordingTime.setVisibility(VISIBLE);
            messageSendButton.setVisibility(GONE);
            voiceMessageButton.setVisibility(GONE);
            recordingTime.setBase(SystemClock.elapsedRealtime());
            recordingTime.start();
        } else {
            recordingTime.stop();
            recordingTime.setBase(SystemClock.elapsedRealtime());
            recordClose.setVisibility(GONE);
            recordTick.setVisibility(GONE);
            recordingTime.setVisibility(GONE);
            messageSendButton.setVisibility(VISIBLE);
            voiceMessageButton.setVisibility(VISIBLE);
        }
    }

    /**
     * This method is called to notify you that, within s,
     * the count characters beginning at start have just replaced old text that had length before
     */
    @Override
    public void onTextChanged(CharSequence s, int start, int count, int after) {
        input = s;
        messageSendButton.setEnabled(input.length() > 0);
        if (s.length() > 0) {
            if (!isTyping) {
                isTyping = true;
                if (typingListener != null) typingListener.onStartTyping();
            }
            removeCallbacks(typingTimerRunnable);
            postDelayed(typingTimerRunnable, delayTypingStatusMillis);
        }
    }

    /**
     * This method is called to notify you that, within s,
     * the count characters beginning at start are about to be replaced by new text with length after.
     */
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        //do nothing
    }

    /**
     * This method is called to notify you that, somewhere within s, the text has been changed.
     */
    @Override
    public void afterTextChanged(Editable editable) {
        //do nothing
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (lastFocus && !hasFocus && typingListener != null) {
            typingListener.onStopTyping();
        }
        lastFocus = hasFocus;
    }

    private boolean onSubmit() {
        return inputListener != null && inputListener.onSubmit(input);
    }

    private void onRecording(boolean isCancel) {
        if (voiceRecordingListener != null) {
            voiceRecordingListener.onRecorded(count, isCancel);
            if (count % 2 == 0) {
                MessageInputStyle style = MessageInputStyle.parse(context, attributeSet);
                this.voiceMessageButton.setImageDrawable(style.getRecordingIcon());
                this.voiceMessageButton.getLayoutParams().width = style.getInputButtonWidth();
                this.voiceMessageButton.getLayoutParams().height = style.getInputButtonHeight();
                ViewCompat.setBackground(voiceMessageButton, style.getInputButtonBackground());
            } else {
                MessageInputStyle style = MessageInputStyle.parse(context, attributeSet);
                this.voiceMessageButton.setImageDrawable(style.getVoiceButtonIcon());
                this.voiceMessageButton.getLayoutParams().width = style.getInputButtonWidth();
                this.voiceMessageButton.getLayoutParams().height = style.getInputButtonHeight();
                ViewCompat.setBackground(voiceMessageButton, style.getInputButtonBackground());
            }
            count++;
        }
    }

    private void onAddAttachments() {
        if (attachmentsListener != null) attachmentsListener.onAddAttachments();
    }

    private void init(Context context, AttributeSet attrs) {
        init(context);
        this.attributeSet = attrs;
        this.context = context;
        MessageInputStyle style = MessageInputStyle.parse(context, attrs);

        this.messageInput.setMaxLines(style.getInputMaxLines());
        this.messageInput.setHint(style.getInputHint());
        this.messageInput.setText(style.getInputText());
        this.messageInput.setTextSize(TypedValue.COMPLEX_UNIT_PX, style.getInputTextSize());
        this.messageInput.setTextColor(style.getInputTextColor());
        this.messageInput.setHintTextColor(style.getInputHintColor());
        ViewCompat.setBackground(this.messageInput, style.getInputBackground());
        setCursor(style.getInputCursorDrawable());

        this.attachmentButton.setVisibility(style.showAttachmentButton() ? VISIBLE : GONE);
        this.attachmentButton.setImageDrawable(style.getAttachmentButtonIcon());
        this.attachmentButton.getLayoutParams().width = style.getAttachmentButtonWidth();
        this.attachmentButton.getLayoutParams().height = style.getAttachmentButtonHeight();
        ViewCompat.setBackground(this.attachmentButton, style.getAttachmentButtonBackground());

        this.attachmentButtonSpace.setVisibility(style.showAttachmentButton() ? VISIBLE : GONE);
        this.attachmentButtonSpace.getLayoutParams().width = style.getAttachmentButtonMargin();

        this.messageSendButton.setImageDrawable(style.getInputButtonIcon());
        this.messageSendButton.getLayoutParams().width = style.getInputButtonWidth();
        this.messageSendButton.getLayoutParams().height = style.getInputButtonHeight();
        ViewCompat.setBackground(messageSendButton, style.getInputButtonBackground());
        this.sendButtonSpace.getLayoutParams().width = style.getInputButtonMargin();

        this.voiceMessageButton.setImageDrawable(style.getVoiceButtonIcon());
        this.voiceMessageButton.getLayoutParams().width = style.getInputButtonWidth();
        this.voiceMessageButton.getLayoutParams().height = style.getInputButtonHeight();
        ViewCompat.setBackground(voiceMessageButton, style.getInputButtonBackground());

        if (getPaddingLeft() == 0
                && getPaddingRight() == 0
                && getPaddingTop() == 0
                && getPaddingBottom() == 0) {
            setPadding(
                    style.getInputDefaultPaddingLeft(),
                    style.getInputDefaultPaddingTop(),
                    style.getInputDefaultPaddingRight(),
                    style.getInputDefaultPaddingBottom()
            );
        }
        this.delayTypingStatusMillis = style.getDelayTypingStatus();
    }

    private void init(Context context) {
        inflate(context, R.layout.view_message_input2, this);

        messageInput = (EditText) findViewById(R.id.messageInput);
        messageSendButton = (ImageButton) findViewById(R.id.messageSendButton);
        voiceMessageButton = (ImageButton) findViewById(R.id.voiceMessgage);
        attachmentButton = (ImageButton) findViewById(R.id.attachmentButton);
        sendButtonSpace = (Space) findViewById(R.id.sendButtonSpace);
        attachmentButtonSpace = (Space) findViewById(R.id.attachmentButtonSpace);

        recordClose = findViewById(R.id.voiceClose);
        recordTick = findViewById(R.id.voiceTick);
        recordingTime = findViewById(R.id.elapsedTime);

        messageSendButton.setOnClickListener(this);
        voiceMessageButton.setOnClickListener(this);
        attachmentButton.setOnClickListener(this);
        recordTick.setOnClickListener(this);
        recordClose.setOnClickListener(this);
        messageInput.addTextChangedListener(this);
        messageInput.setText("");
        messageInput.setOnFocusChangeListener(this);
    }

    private void setCursor(Drawable drawable) {
        if (drawable == null) return;

        try {
            final Field drawableResField = TextView.class.getDeclaredField("mCursorDrawableRes");
            drawableResField.setAccessible(true);

            final Object drawableFieldOwner;
            final Class<?> drawableFieldClass;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                drawableFieldOwner = this.messageInput;
                drawableFieldClass = TextView.class;
            } else {
                final Field editorField = TextView.class.getDeclaredField("mEditor");
                editorField.setAccessible(true);
                drawableFieldOwner = editorField.get(this.messageInput);
                drawableFieldClass = drawableFieldOwner.getClass();
            }
            final Field drawableField = drawableFieldClass.getDeclaredField("mCursorDrawable");
            drawableField.setAccessible(true);
            drawableField.set(drawableFieldOwner, new Drawable[]{drawable, drawable});
        } catch (Exception ignored) {
        }
    }

    public void setTypingListener(TypingListener typingListener) {
        this.typingListener = typingListener;
    }

    /**
     * Interface definition for a callback to be invoked when user pressed 'submit' button
     */
    public interface InputListener {

        /**
         * Fires when user presses 'send' button.
         *
         * @param input input entered by user
         * @return if input text is valid, you must return {@code true} and input will be cleared, otherwise return false.
         */
        boolean onSubmit(CharSequence input);
    }

    public interface VoiceRecordingListener {

        /**
         * Fires when user presses 'send' button.
         *
         * @return if input text is valid, you must return {@code true} and input will be cleared, otherwise return false.
         */
        void onRecorded(int count, boolean isCancel);
    }

    /**
     * Interface definition for a callback to be invoked when user presses 'add' button
     */
    public interface AttachmentsListener {

        /**
         * Fires when user presses 'add' button.
         */
        void onAddAttachments();
    }

    /**
     * Interface definition for a callback to be invoked when user typing
     */
    public interface TypingListener {

        /**
         * Fires when user presses start typing
         */
        void onStartTyping();

        /**
         * Fires when user presses stop typing
         */
        void onStopTyping();

    }
}
