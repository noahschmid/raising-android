package com.raising.app.models;

import java.io.Serializable;

import lombok.Data;

@Data
public class ImageCacheItem implements Serializable {
    long id;
    String filename;
}
