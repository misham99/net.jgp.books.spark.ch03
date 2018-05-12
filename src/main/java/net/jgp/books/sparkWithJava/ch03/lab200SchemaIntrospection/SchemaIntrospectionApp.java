package net.jgp.books.sparkWithJava.ch03.lab200SchemaIntrospection;

import static org.apache.spark.sql.functions.concat;
import static org.apache.spark.sql.functions.lit;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.StructType;

/**
 * Introspection of a schema.
 * 
 * @author jgp
 */
public class SchemaIntrospectionApp {

  /**
   * main() is your entry point to the application.
   * 
   * @param args
   */
  public static void main(String[] args) {
    SchemaIntrospectionApp app = new SchemaIntrospectionApp();
    app.start();
  }

  /**
   * The processing code.
   */
  private void start() {
    // Creates a session on a local master
    SparkSession spark = SparkSession.builder()
        .appName("Restaurants in Wake County, NC")
        .master("local")
        .getOrCreate();

    // Reads a CSV file with header, called books.csv, stores it in a dataframe
    Dataset<Row> df = spark.read().format("csv")
        .option("header", "true")
        .load("data/Restaurants_in_Wake_County_NC.csv");
    System.out.println("*** Right after ingestion");
    df.show(5);
    df.printSchema();
    System.out.println("We have " + df.count() + " records.");

    // Let's transform our dataframe
    df = df.withColumn("county", lit("Wake"))
        .withColumnRenamed("HSISID", "datasetId")
        .withColumnRenamed("NAME", "name")
        .withColumnRenamed("ADDRESS1", "address1")
        .withColumnRenamed("ADDRESS2", "address2")
        .withColumnRenamed("CITY", "city")
        .withColumnRenamed("STATE", "state")
        .withColumnRenamed("POSTALCODE", "zip")
        .withColumnRenamed("PHONENUMBER", "tel")
        .withColumnRenamed("RESTAURANTOPENDATE", "dateStart")
        .withColumnRenamed("FACILITYTYPE", "type")
        .withColumnRenamed("X", "geoX")
        .withColumnRenamed("Y", "geoY");
    df = df.withColumn("id", concat(
        df.col("state"),
        lit("_"),
        df.col("county"), lit("_"),
        df.col("datasetId")));

    // Shows at most 5 rows from the dataframe
    System.out.println("*** Dataframe transformed");
    df.show(5);
    df.printSchema();

    StructType schema = df.schema();
    schema.printTreeString();
    String schemaAsString = schema.mkString();
    System.out.println("Schema as string: " + schemaAsString);
    String schemaAsJson = schema.prettyJson();
    System.out.println("Schema as JSON: " + schemaAsJson);
  }
}
