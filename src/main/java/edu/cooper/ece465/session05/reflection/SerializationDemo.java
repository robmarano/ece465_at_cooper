package edu.cooper.ece465.session05.reflection;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.cooper.ece465.session05.gson.model.Employee;
import edu.cooper.ece465.session05.gson.model.Address;

public class SerializationDemo {
    public static void main(String[] args) {
        try {
            // 1. Setup Data
            Employee emp = new Employee();
            emp.setId(42);
            emp.setName("Alice");
            emp.setRole("Engineer");
            emp.setPermanent(true);

            Address addr = new Address();
            addr.setCity("New York");
            addr.setStreet("Cooper Square");
            addr.setZipcode(10003);
            emp.setAddress(addr);

            // 2. Gson Serialization (The Gold Standard)
            System.out.println("--- Gson Output ---");
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            System.out.println(gson.toJson(emp));

            // 3. MiniJsonSerializer (Our Implementation)
            System.out.println("\n--- MiniJsonSerializer Output ---");
            MiniJsonSerializer serializer = new MiniJsonSerializer();
            String json = serializer.toJson(emp);
            System.out.println(json);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
