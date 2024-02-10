package com.example.bigdata;

import com.espertech.esper.common.client.EPCompiled;
import com.espertech.esper.common.client.EventBean;
import com.espertech.esper.common.client.configuration.Configuration;
import com.espertech.esper.compiler.client.CompilerArguments;
import com.espertech.esper.compiler.client.EPCompileException;
import com.espertech.esper.compiler.client.EPCompiler;
import com.espertech.esper.compiler.client.EPCompilerProvider;
import com.espertech.esper.runtime.client.*;
import net.datafaker.Faker;
import net.datafaker.fileformats.Format;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class EsperClient {
    public static void main(String[] args) throws InterruptedException {
        int noOfRecordsPerSec;
        int howLongInSec;
        if (args.length < 2) {
            noOfRecordsPerSec = 2;
            howLongInSec = 5;
        } else {
            noOfRecordsPerSec = Integer.parseInt(args[0]);
            howLongInSec = Integer.parseInt(args[1]);
        }

        Configuration config = new Configuration();
        EPCompiled epCompiled = getEPCompiled(config);

        // Connect to the EPRuntime server and deploy the statement
        EPRuntime runtime = EPRuntimeProvider.getRuntime("http://localhost:port", config);
        EPDeployment deployment;
        try {
            deployment = runtime.getDeploymentService().deploy(epCompiled);
        }
        catch (EPDeployException ex) {
            // handle exception here
            throw new RuntimeException(ex);
        }

        EPStatement resultStatement = runtime.getDeploymentService().getStatement(deployment.getDeploymentId(), "result");

        // Add a listener to the statement to handle incoming events
        resultStatement.addListener( (newData, oldData, stmt, runTime) -> {
            for (EventBean eventBean : newData) {
                System.out.printf("R: %s%n", eventBean.getUnderlying());
            }
        });

//        Faker faker = new Faker();
        String record;

        // Lista marek samochodów
        List<String> carBrands = Arrays.asList("Toyota", "Ford", "Chevrolet", "Honda", "Nissan", "BMW", "Mercedes-Benz", "Audi", "Volvo", "Volkswagen");
        Faker faker = new Faker();

        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() < startTime + (1000L * howLongInSec)) {
            for (int i = 0; i < noOfRecordsPerSec; i++) {
                String carBrand = carBrands.get(faker.number().numberBetween(0, carBrands.size()));
                Timestamp etimestamp = faker.date().past(30, TimeUnit.SECONDS);
                etimestamp.setNanos(0);
                Timestamp itimestamp = Timestamp.valueOf(LocalDateTime.now().withNano(0));

                // Losowy rok produkcji
                int yearOfManufacture = faker.number().numberBetween(1990, 2020); // 1990-2020

                // Losowa cena
                int price = faker.number().numberBetween(4000, 350000); // 4 000 - 350 000

                // Losowy przebieg
                int mileage = faker.number().numberBetween(100, 250000); // 100 - 250 000

                // Generowanie rekordu w formacie JSON
                record = Format.toJson()
                        .set("brand", () -> carBrand)
                        .set("year", () -> String.valueOf(yearOfManufacture))
                        .set("price", () -> String.valueOf(price))
                        .set("mileage", () -> String.valueOf(mileage))
                        .set("ets", etimestamp::toString)
                        .set("its", itimestamp::toString)

                        .build().generate();
                runtime.getEventService().sendEventJson(record, "CarSaleEvent");
            }
            waitToEpoch();
        }
    }

    private static EPCompiled getEPCompiled(Configuration config) {
        CompilerArguments compilerArgs = new CompilerArguments(config);

        // Compile the EPL statement
        EPCompiler compiler = EPCompilerProvider.getCompiler();
        EPCompiled epCompiled;
        try {
            epCompiled = compiler.compile("""
                    @public @buseventtype create json schema CarSaleEvent(brand string, `year` int,
                    mileage int, price int, ets string, its string);

                    @name('result') SELECT brand, price, mileage, `year`, ets, its
                    FROM CarSaleEvent#ext_timed(java.sql.Timestamp.valueOf(its).getTime(), 3 sec)
                    """, compilerArgs);
        }
        catch (EPCompileException ex) {
            // handle exception here
            throw new RuntimeException(ex);
        }
        return epCompiled;
    }

    static void waitToEpoch() throws InterruptedException {
        long millis = System.currentTimeMillis();
        Instant instant = Instant.ofEpochMilli(millis) ;
        Instant instantTrunc = instant.truncatedTo( ChronoUnit.SECONDS ) ;
        long millis2 = instantTrunc.toEpochMilli() ;
        TimeUnit.MILLISECONDS.sleep(millis2+1000-millis);
    }
}

