config create_schema: false, load_new: true

import com.datastax.driver.dse.geometry.Point

locationsFile = File.csv(inputfilename).delimiter('|')

locationsFile = locationsFile.transform {
    it['point'] = Point.fromWellKnownText(it['point']);
    return it;
}

load(locationsFile).asVertices {
  label "location"
  key "name"
}
