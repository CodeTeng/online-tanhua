package com.tanhua.model.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/5 14:59
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "places")
@CompoundIndex(name = "location_index", def = "{'location': '2dsphere'}")
public class Places {
    private ObjectId id;
    private String title;
    private String address;
    private GeoJsonPoint location;
}
