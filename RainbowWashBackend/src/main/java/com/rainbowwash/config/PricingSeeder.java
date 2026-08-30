package com.rainbowwash.config;

import com.rainbowwash.model.LaundryService;
import com.rainbowwash.repository.LaundryServiceRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

// Runs once on every startup, but only ever INSERTS default rows for a
// category that currently has zero rows — so the first deploy after this
// change populates real starting prices (matching what was previously
// hardcoded on the frontend), and every deploy after that is a no-op here,
// even if staff have since edited or deleted individual prices.
@Component
public class PricingSeeder implements CommandLineRunner {

    private final LaundryServiceRepository repository;

    public PricingSeeder(LaundryServiceRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        seedIfEmpty("Self Wash", new Object[][]{
                {"Wash & Dry", 1500, null, null},
                {"Drying Only", 1000, null, null},
                {"Wash & Iron", 4000, null, null},
        });

        seedIfEmpty("Staff Wash", new Object[][]{
                {"Wash & Dry", 2000, null, null},
                {"Ironing Only", 2500, null, null},
                {"Wash & Iron", 4500, null, null},
                {"Drying Only", 1500, null, null},
        });

        seedIfEmpty("Dry Cleaning", new Object[][]{
                {"Suit (2-piece)", 5000, 7000, null},
                {"Suit (3-piece)", 7000, 10000, null},
                {"Blazer", 2000, 4000, null},
                {"Coat", 4000, 5000, null},
                {"Leather Jacket", 3000, 4000, null},
        });

        seedIfEmpty("Shoe Care", new Object[][]{
                {"Sneakers", 4000, 8000, 2000},
                {"Suedes", 8000, 10000, 3000},
                {"Heels & Boots", 5000, 7000, 2000},
                {"Leather Shoes", 4000, 5000, 2000},
        });

        seedIfEmpty("Express", new Object[][]{
                {"Express Laundry (same-day)", 2000, null, null},
                {"Express Upholstery (same-day)", 5000, null, null},
                {"Express Cleaning (same-day)", 5000, null, null},
        });

        seedIfEmpty("Detergents", new Object[][]{
                {"Fabric Softener (small)", 2500, null, null},
                {"Fabric Softener (big)", 25000, null, null},
                {"Liquid Detergent (small)", 2500, null, null},
                {"Liquid Detergent (big)", 25000, null, null},
                {"So Klin Smart", 500, null, null},
                {"So Klin Detergent (small)", 1000, null, null},
                {"Viva Detergent (small)", 700, null, null},
                {"Viva Detergent (big)", 3000, null, null},
        });

        seedIfEmpty("Starch", new Object[][]{
                {"Starch, Original Lavender Niagara (per cloth)", 500, null, null},
                {"Starch, Heavy Lavender Faultless (per cloth)", 500, null, null},
                {"Starch, Heavy Amindon Lourd Braxton5 (per cloth)", 500, null, null},
        });

        seedIfEmpty("Bleach", new Object[][]{
                {"Bleach (small)", 1500, null, null},
                {"Bleach (big)", 4500, null, null},
        });

        seedIfEmpty("Nylon", new Object[][]{
                {"Nylon XL", 1500, null, null},
                {"Nylon L", 1000, null, null},
                {"Nylon M", 500, null, null},
                {"Nylon S", 300, null, null},
        });

        seedIfEmpty("Bags", new Object[][]{
                {"Shoe Bag", 1000, null, null},
                {"Suite Bag", 4000, null, null},
                {"Bag", 5500, null, null},
        });

        seedIfEmpty("Extras", new Object[][]{
                {"Minor Stain Remover (per cloth)", 500, null, null},
                {"Regular Stain Remover (per cloth)", 1000, null, null},
                {"Tuff Stain Remover (per cloth)", 2000, null, null},
                {"Scent Beads (per cap)", 500, null, null},
                {"Tiepod", 1000, null, null},
        });

        seedIfEmpty("Home Cleaning", new Object[][]{
                {"Studio / 1 Bed", 12000, null, null},
                {"2–3 Bedroom", 20000, null, null},
                {"Duplex / 4+ Bed", 32000, null, null},
        });

        seedIfEmpty("Office Cleaning", new Object[][]{
                {"Small office", 15000, null, null},
                {"Medium office", 28000, null, null},
                {"Large office / floor", 45000, null, null},
        });

        seedIfEmpty("Deep Cleaning", new Object[][]{
                {"Studio / 1 Bed", 22000, null, null},
                {"2–3 Bedroom", 36000, null, null},
                {"Duplex / 4+ Bed", 55000, null, null},
        });

        seedIfEmpty("Upholstery Cleaning", new Object[][]{
                {"Single item (chair/mattress)", 8000, null, null},
                {"3-seater sofa", 16000, null, null},
                {"Full living room set", 30000, null, null},
        });
    }

    private void seedIfEmpty(String category, Object[][] rows) {
        if (repository.existsByCategory(category)) return;

        for (Object[] row : rows) {
            LaundryService s = new LaundryService();
            s.setName((String) row[0]);
            s.setDescription("");
            s.setPrice(toBigDecimal(row[1]));
            s.setDeepPrice(toBigDecimal(row[2]));
            s.setRepairPrice(toBigDecimal(row[3]));
            s.setCategory(category);
            s.setStock(5);
            s.setAvailable(true);
            repository.save(s);
        }
    }

    private BigDecimal toBigDecimal(Object value) {
        return value == null ? null : new BigDecimal(value.toString());
    }
}