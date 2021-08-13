package com.hyphenate.easeim.app.utils;


import com.hyphenate.easecallkit.widget.EaseImageView;
import com.hyphenate.easeim.app.api.old_http.AppConfig;
import com.hyphenate.easeui.EaseIM;
import com.hyphenate.easeui.domain.EaseAvatarOptions;

public class ImageUtil {


    public static String checkimg(String path) {
        if (path == null) {
            return "";
        }
        if (path.contains("http://") || path.contains("https://")) {
            return path;
        } else {
            return AppConfig.ImageMainUrl + path;
        }
    }

    /**
     * 设置头像形状
     *
     * @param userAvatarView
     */
    public static void setAvatar(EaseImageView userAvatarView) {
        EaseAvatarOptions avatarOptions = EaseIM.getInstance().getAvatarOptions();
        if (avatarOptions != null && userAvatarView instanceof EaseImageView) {
            EaseImageView avatarView = ((EaseImageView) userAvatarView);
            if (avatarOptions.getAvatarShape() != 0) {
                avatarView.setShapeType(avatarOptions.getAvatarShape());
            }
            if (avatarOptions.getAvatarBorderWidth() != 0) {
                avatarView.setBorderWidth(avatarOptions.getAvatarBorderWidth());
            }
            if (avatarOptions.getAvatarBorderColor() != 0) {
                avatarView.setBorderColor(avatarOptions.getAvatarBorderColor());
            }
            if (avatarOptions.getAvatarRadius() != 0) {
                avatarView.setRadius(avatarOptions.getAvatarRadius());
            }
        }
    }

}
