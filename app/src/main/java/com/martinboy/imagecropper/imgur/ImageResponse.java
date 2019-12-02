/*
 *  Copyright (c) 2019 Mattel, Inc. All rights reserved.
 *  All information and code contained herein is the property of Mattel, Inc.
 *  Any unauthorized use, storage, duplication, and redistribution of this
 *  material without written permission from Mattel, Inc. is strictly prohibited.
 *
 *
 */

package com.martinboy.imagecropper.imgur;

public class ImageResponse {
    public boolean success;
    public int status;
    public UploadedImage data;

    public static class UploadedImage{
        public String id;
        public String title;
        public String description;
        public String type;
        public boolean animated;
        public int width;
        public int height;
        public int size;
        public int views;
        public int bandwidth;
        public String vote;
        public boolean favorite;
        public String account_url;
        public String deletehash;
        public String name;
        public String link;

        @Override public String toString() {
            return "UploadedImage{" +
                    "id='" + id + '\'' +
                    ", title='" + title + '\'' +
                    ", description='" + description + '\'' +
                    ", type='" + type + '\'' +
                    ", animated=" + animated +
                    ", width=" + width +
                    ", height=" + height +
                    ", size=" + size +
                    ", views=" + views +
                    ", bandwidth=" + bandwidth +
                    ", vote='" + vote + '\'' +
                    ", favorite=" + favorite +
                    ", account_url='" + account_url + '\'' +
                    ", deletehash='" + deletehash + '\'' +
                    ", name='" + name + '\'' +
                    ", link='" + link + '\'' +
                    '}';
        }
    }

    @Override public String toString() {
        return "ImageResponse{" +
                "success=" + success +
                ", status=" + status +
                ", data=" + data.toString() +
                '}';
    }
}