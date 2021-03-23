package com.tomilin.fitnessTracker;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;

public class Serializer {
    private final JAXBContext context;
    private final String profilesFile;

    public Serializer(String profilesFile) throws JAXBException {
        this.profilesFile = profilesFile;
        this.context = JAXBContext.newInstance(Users.class);
    }

    public void serialize(Users users) throws JAXBException {
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        try {
            marshaller.marshal(users, new FileOutputStream(profilesFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public List<User> deserialize() throws JAXBException {
        File file = new File(profilesFile);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        return ((Users)unmarshaller.unmarshal(file)).getUsers();
    }
}
