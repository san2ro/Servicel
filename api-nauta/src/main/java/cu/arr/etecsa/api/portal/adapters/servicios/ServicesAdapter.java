package cu.arr.etecsa.api.portal.adapters.servicios;

import androidx.annotation.Keep;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonWriter;
import com.squareup.moshi.Moshi;
import cu.arr.etecsa.api.portal.adapters.correo.CorreoPerfilAdapter;
import cu.arr.etecsa.api.portal.adapters.mobile.MobilePerfilAdapter;
import cu.arr.etecsa.api.portal.adapters.navigation.NavigationPerfilAdapter;
import cu.arr.etecsa.api.portal.adapters.telephony.TFPerfilAdapter;
import cu.arr.etecsa.api.portal.models.servicios.correo.CorreoPerfil;
import cu.arr.etecsa.api.portal.models.servicios.mobile.MobilePerfil;
import cu.arr.etecsa.api.portal.models.servicios.navigation.NavigationPerfil;
import cu.arr.etecsa.api.portal.models.servicios.telephony.TFPerfil;
import java.io.IOException;
import com.squareup.moshi.JsonReader;
import cu.arr.etecsa.api.portal.models.servicios.Services;
import java.util.HashMap;
import java.util.Map;

@Keep
public class ServicesAdapter extends JsonAdapter<Services> {

    private JsonReader.Options options =
            JsonReader.Options.of(
                    "Navegación", "Servicios móviles", "Correo Nauta", "Telefonía fija");

    private Moshi moshi =
            new Moshi.Builder()
                    .add(MobilePerfil.class, new MobilePerfilAdapter())
                    .add(NavigationPerfil.class, new NavigationPerfilAdapter())
                    .add(CorreoPerfil.class, new CorreoPerfilAdapter())
                    .add(TFPerfil.class, new TFPerfilAdapter())
                    .build();

    private JsonAdapter<MobilePerfil> mobilePerfil = moshi.adapter(MobilePerfil.class);
    private JsonAdapter<NavigationPerfil> navigationPerfil = moshi.adapter(NavigationPerfil.class);
    private JsonAdapter<CorreoPerfil> correoPerfil = moshi.adapter(CorreoPerfil.class);
    private JsonAdapter<TFPerfil> tfPerfil = moshi.adapter(TFPerfil.class);

    @Override
    public Services fromJson(JsonReader reader) throws IOException {
        if (reader.peek() != JsonReader.Token.BEGIN_OBJECT) {
            // Si Servicios es un string como "no disponible", lo ignoramos
            reader.skipValue();
            return new Services(new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>());
        }

        Map<String, MobilePerfil> mobileServices = new HashMap<>();
        Map<String, NavigationPerfil> navigationServices = new HashMap<>();
        Map<String, CorreoPerfil> correoServices = new HashMap<>();
        Map<String, TFPerfil> telefoniaService = new HashMap<>();

        reader.beginObject();
        while (reader.hasNext()) {
            int index = reader.selectName(options);
            switch (index) {
                case 0: // Navegación
                    if (reader.peek() == JsonReader.Token.BEGIN_OBJECT) {
                        reader.beginObject();
                        while (reader.hasNext()) {
                            String clave = reader.nextName();
                            if (reader.peek() == JsonReader.Token.BEGIN_OBJECT) {
                                reader.beginObject();
                                while (reader.hasNext()) {
                                    String subclave = reader.nextName();
                                    if (subclave.equals("perfil")) {
                                        NavigationPerfil navPerfil =
                                                navigationPerfil.fromJson(reader);
                                        if (navPerfil != null) {
                                            navigationServices.put(clave, navPerfil);
                                        }
                                    } else {
                                        reader.skipValue();
                                    }
                                }
                                reader.endObject();
                            } else {
                                reader.skipValue();
                            }
                        }
                        reader.endObject();
                    } else {
                        reader.skipValue();
                    }
                    break;

                case 1: // Servicios móviles
                    if (reader.peek() == JsonReader.Token.BEGIN_OBJECT) {
                        reader.beginObject();
                        while (reader.hasNext()) {
                            String clave = reader.nextName();
                            if (reader.peek() == JsonReader.Token.BEGIN_OBJECT) {
                                reader.beginObject();
                                while (reader.hasNext()) {
                                    String subclave = reader.nextName();
                                    if (subclave.equals("perfil")) {
                                        MobilePerfil perfil = mobilePerfil.fromJson(reader);
                                        if (perfil != null) {
                                            mobileServices.put(clave, perfil);
                                        }
                                    } else {
                                        reader.skipValue();
                                    }
                                }
                                reader.endObject();
                            } else {
                                reader.skipValue();
                            }
                        }
                        reader.endObject();
                    } else {
                        reader.skipValue();
                    }
                    break;

                case 2: // Correo Nauta
                    if (reader.peek() == JsonReader.Token.BEGIN_OBJECT) {
                        reader.beginObject();
                        while (reader.hasNext()) {
                            String clave = reader.nextName();
                            if (reader.peek() == JsonReader.Token.BEGIN_OBJECT) {
                                reader.beginObject();
                                while (reader.hasNext()) {
                                    String subclave = reader.nextName();
                                    if (subclave.equals("perfil")) {
                                        CorreoPerfil perfil = correoPerfil.fromJson(reader);
                                        if (perfil != null) {
                                            correoServices.put(clave, perfil);
                                        }
                                    } else {
                                        reader.skipValue();
                                    }
                                }
                                reader.endObject();
                            } else {
                                reader.skipValue();
                            }
                        }
                        reader.endObject();
                    } else {
                        reader.skipValue();
                    }
                    break;

                case 3: // Telefonía fija
                    if (reader.peek() == JsonReader.Token.BEGIN_OBJECT) {
                        reader.beginObject();
                        while (reader.hasNext()) {
                            String clave = reader.nextName();
                            if (reader.peek() == JsonReader.Token.BEGIN_OBJECT) {
                                reader.beginObject();
                                while (reader.hasNext()) {
                                    String subclave = reader.nextName();
                                    if (subclave.equals("perfil")) {
                                        TFPerfil perfil = tfPerfil.fromJson(reader);
                                        if (perfil != null) {
                                            telefoniaService.put(clave, perfil);
                                        }
                                    } else {
                                        reader.skipValue();
                                    }
                                }
                                reader.endObject();
                            } else {
                                reader.skipValue();
                            }
                        }
                        reader.endObject();
                    } else {
                        reader.skipValue();
                    }
                    break;

                default:
                    reader.skipValue();
                    break;
            }
        }
        reader.endObject();
        return new Services(mobileServices, navigationServices, correoServices, telefoniaService);
    }

    @Override
    public void toJson(JsonWriter writer, Services value) throws IOException {
        throw new UnsupportedOperationException("toJson not supported");
    }
}

/*

@Override
public Services fromJson(JsonReader reader) throws IOException {
    if (reader.peek() != JsonReader.Token.BEGIN_OBJECT) {
        // Si Servicios es un string como "no disponible", lo ignoramos
        reader.skipValue();
        return new Services(new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>());
    }

    Map<String, MobilePerfil> mobileServices = new HashMap<>();
    Map<String, NavigationPerfil> navigationServices = new HashMap<>();
    Map<String, CorreoPerfil> correoServices = new HashMap<>();
    Map<String, TFPerfil> telefoniaService = new HashMap<>();

    reader.beginObject();
    while (reader.hasNext()) {
        int index = reader.selectName(options);
        switch (index) {
            case 0: // Navegación
                ...
            case 1: // Servicios móviles
                ...
            case 2: // Correo Nauta
                ...
            case 3: // Telefonía fija
                ...
            default:
                reader.skipValue();
                break;
        }
    }
    reader.endObject();
    return new Services(mobileServices, navigationServices, correoServices, telefoniaService);
}


*/
