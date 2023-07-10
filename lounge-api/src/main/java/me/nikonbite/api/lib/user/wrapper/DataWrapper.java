package me.nikonbite.api.lib.user.wrapper;

import me.nikonbite.api.LoungeAPI;
import me.nikonbite.api.lib.user.group.Group;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public interface DataWrapper {

    static int getId(String name) {

        AtomicInteger atomicInteger = new AtomicInteger(-1);

        LoungeAPI.hikariConnection.executeQuery("SELECT * FROM Identifiers WHERE `Name`=?", rs -> {

            if (rs.next()) {
                atomicInteger.set(rs.getInt("Id"));
            }

            return null;
        }, name);

        return atomicInteger.get();
    }

    static String getName(int id) {

        AtomicReference<String> atomicReference = new AtomicReference<>(null);

        LoungeAPI.hikariConnection.executeQuery("SELECT * FROM Identifiers WHERE `Id`=?", rs -> {

            if (rs.next()) {
                atomicReference.set(rs.getString("Name"));
            }

            return null;
        }, id);

        return atomicReference.get();
    }

    static Group getGroup(int id) {
        AtomicReference<Group> reference = new AtomicReference<>(null);

        LoungeAPI.hikariConnection.executeQuery("SELECT * FROM `Groups` WHERE `Id`=?", rs -> {
            if (rs.next()) {
                reference.set(Group.valueOf(rs.getString("Group")));
            }
            return null;
        }, id);

        return reference.get();
    }
}
