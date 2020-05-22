package cafe.adriel.androidaudioconverter.sample;

/**
 * Copyright (c) by Elias Herbst, Joaquin Martinez, Christoffer Quick and Simon Waidringer 2018.
 * This program is distributed under a Creative Common Attribution-ShareAlike 4.0 licence.
 * You are free to share, copy, distribute, transmit the work, to remix and adapt the work but
 * you must provide the attribution to Elias, Joaquin, Christoffer, Simon and ShareAlike in kind.
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This class provides the connection to the database which stores the filter information.
 * The class allows the access to the values for the low and high pass cut of frequencies
 * for a certain filter id.
 */

public class Database {

    //--------------------Connection Variables----------------------------------------
    private String connectionInfo = "jdbc:mysql://mysql.u4436252.fsdata.se/u4436252_a";
    private String userName = "u4436252";
    private String password = "6+hvxjPqMKU7";

    private Connection connection = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;

    //--------------------Variables----------------------------------------
    private int highpass;
    private int lowpass;
    //--------------------Variables declaration end--------------------

    //Gets the information from the database for the certain filter id
    public Database(int id) {

        try {

            connection = DriverManager.getConnection(connectionInfo, userName, password);

            String query = "SELECT highpass, lowpass FROM filter WHERE idfilter = ?;";

            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {

                highpass = resultSet.getInt(1);
                lowpass = resultSet.getInt(2);

            }//end if

        } catch (SQLException ex) {

            ex.printStackTrace();

        } finally {

            close();

        }//end try

    }//end constructor


    private void close() {
        try {

            if (resultSet != null) {

                resultSet.close();
            }

            if (preparedStatement != null) {

                preparedStatement.close();
            }

            if (connection != null) {

                connection.close();
            }

        } catch (Exception ex) {

            ex.printStackTrace();
        }
    }// End of close()

    //Returns the high pass values for the filter
    public int getHighpass() {
        return highpass;
    }//end getHighpass

    //Returns the low pass values for the filter
    public int getLowpass() {
        return lowpass;
    }//end getLowpass

    @Override
    public String toString() {
        return "Database{" +
                "highpass=" + highpass +
                ", lowpass=" + lowpass +
                '}';
    }
}
