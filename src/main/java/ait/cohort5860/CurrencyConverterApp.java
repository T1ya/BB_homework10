package ait.cohort5860;

import ait.cohort5860.config.ConfigLoader;
import ait.cohort5860.dto.FixerApiDto;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;
import java.util.Scanner;

public class CurrencyConverterApp {
    private static final String URL = "http://data.fixer.io/api/latest";
    private static final String KEY = ConfigLoader.get("fixer.api.key");

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.print("FROM currency: ");
        String from = scanner.nextLine().toUpperCase();
        System.out.print("TO currency: ");
        String to = scanner.nextLine().toUpperCase();
        System.out.print("Amount to convert: ");
        double amount = 0;
        try {
            amount = Double.parseDouble(scanner.nextLine());
            if (amount <= 0) {
                System.out.println("Error - amount must be greater than zero");
                return;
            }
        }
        catch (NumberFormatException e) {
            System.out.println("Error - amount must be a number");
            return;
        }

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(URL)
                .queryParam("access_key", KEY);
        URI url = builder.build().encode().toUri();

        RequestEntity<Void> request = new RequestEntity<>(HttpMethod.GET, url);
        ResponseEntity<FixerApiDto> response = new RestTemplate().exchange(request, FixerApiDto.class);
        assert response.getBody() != null;
        FixerApiDto fixer = response.getBody();
        if (!fixer.isSuccess()) {
            System.out.println("Error - can't get data from FixerApi.");
            return;
        }
        Map<String, Double> rates = fixer.getRates();
        Double rateFrom = rates.get(from);
        Double rateTo = rates.get(to);
        if (rateFrom != null && rateTo != null) {
            double result = (amount / rateFrom) * rateTo;
            System.out.printf("%.2f %s = %.2f %s (course date: %s)%n", amount, from, result, to, fixer.getDate());
            return;
        }
        System.out.println("Error - please enter valid currency code.");
        System.out.println("Available currencies: " + rates.keySet());
    }
}
