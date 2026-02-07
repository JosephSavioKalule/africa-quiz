#!/usr/bin/env python3
"""
Scrapes African capital cities data from Wikipedia and outputs JSON.
Falls back to hardcoded data if scraping fails.
"""

import json
import os
import sys

try:
    import requests
    from bs4 import BeautifulSoup
    HAS_DEPS = True
except ImportError:
    HAS_DEPS = False

# Hardcoded fallback data: all 54 African countries with capitals and coordinates
FALLBACK_COUNTRIES = [
    {"name": "Algeria", "capital": "Algiers", "lat": 36.7538, "lng": 3.0588},
    {"name": "Angola", "capital": "Luanda", "lat": -8.8390, "lng": 13.2894},
    {"name": "Benin", "capital": "Porto-Novo", "lat": 6.4969, "lng": 2.6289},
    {"name": "Botswana", "capital": "Gaborone", "lat": -24.6282, "lng": 25.9231},
    {"name": "Burkina Faso", "capital": "Ouagadougou", "lat": 12.3714, "lng": -1.5197},
    {"name": "Burundi", "capital": "Gitega", "lat": -3.4264, "lng": 29.9246},
    {"name": "Cabo Verde", "capital": "Praia", "lat": 14.9331, "lng": -23.5133},
    {"name": "Cameroon", "capital": "Yaoundé", "lat": 3.8480, "lng": 11.5021},
    {"name": "Central African Republic", "capital": "Bangui", "lat": 4.3947, "lng": 18.5582},
    {"name": "Chad", "capital": "N'Djamena", "lat": 12.1348, "lng": 15.0557},
    {"name": "Comoros", "capital": "Moroni", "lat": -11.7172, "lng": 43.2551},
    {"name": "Democratic Republic of the Congo", "capital": "Kinshasa", "lat": -4.4419, "lng": 15.2663},
    {"name": "Republic of the Congo", "capital": "Brazzaville", "lat": -4.2634, "lng": 15.2429},
    {"name": "Côte d'Ivoire", "capital": "Yamoussoukro", "lat": 6.8276, "lng": -5.2893},
    {"name": "Djibouti", "capital": "Djibouti", "lat": 11.5880, "lng": 43.1456},
    {"name": "Egypt", "capital": "Cairo", "lat": 30.0444, "lng": 31.2357},
    {"name": "Equatorial Guinea", "capital": "Malabo", "lat": 3.7504, "lng": 8.7371},
    {"name": "Eritrea", "capital": "Asmara", "lat": 15.3229, "lng": 38.9251},
    {"name": "Eswatini", "capital": "Mbabane", "lat": -26.3054, "lng": 31.1367},
    {"name": "Ethiopia", "capital": "Addis Ababa", "lat": 9.0250, "lng": 38.7469},
    {"name": "Gabon", "capital": "Libreville", "lat": 0.4162, "lng": 9.4673},
    {"name": "Gambia", "capital": "Banjul", "lat": 13.4549, "lng": -16.5790},
    {"name": "Ghana", "capital": "Accra", "lat": 5.6037, "lng": -0.1870},
    {"name": "Guinea", "capital": "Conakry", "lat": 9.6412, "lng": -13.5784},
    {"name": "Guinea-Bissau", "capital": "Bissau", "lat": 11.8037, "lng": -15.1804},
    {"name": "Kenya", "capital": "Nairobi", "lat": -1.2921, "lng": 36.8219},
    {"name": "Lesotho", "capital": "Maseru", "lat": -29.3167, "lng": 27.4833},
    {"name": "Liberia", "capital": "Monrovia", "lat": 6.2907, "lng": -10.7605},
    {"name": "Libya", "capital": "Tripoli", "lat": 32.8872, "lng": 13.1913},
    {"name": "Madagascar", "capital": "Antananarivo", "lat": -18.8792, "lng": 47.5079},
    {"name": "Malawi", "capital": "Lilongwe", "lat": -13.9626, "lng": 33.7741},
    {"name": "Mali", "capital": "Bamako", "lat": 12.6392, "lng": -8.0029},
    {"name": "Mauritania", "capital": "Nouakchott", "lat": 18.0735, "lng": -15.9582},
    {"name": "Mauritius", "capital": "Port Louis", "lat": -20.1609, "lng": 57.5012},
    {"name": "Morocco", "capital": "Rabat", "lat": 34.0209, "lng": -6.8416},
    {"name": "Mozambique", "capital": "Maputo", "lat": -25.9692, "lng": 32.5732},
    {"name": "Namibia", "capital": "Windhoek", "lat": -22.5609, "lng": 17.0658},
    {"name": "Niger", "capital": "Niamey", "lat": 13.5116, "lng": 2.1254},
    {"name": "Nigeria", "capital": "Abuja", "lat": 9.0765, "lng": 7.3986},
    {"name": "Rwanda", "capital": "Kigali", "lat": -1.9706, "lng": 30.1044},
    {"name": "São Tomé and Príncipe", "capital": "São Tomé", "lat": 0.1864, "lng": 6.6131},
    {"name": "Senegal", "capital": "Dakar", "lat": 14.7167, "lng": -17.4677},
    {"name": "Seychelles", "capital": "Victoria", "lat": -4.6191, "lng": 55.4513},
    {"name": "Sierra Leone", "capital": "Freetown", "lat": 8.4657, "lng": -13.2317},
    {"name": "Somalia", "capital": "Mogadishu", "lat": 2.0469, "lng": 45.3182},
    {"name": "South Africa", "capital": "Pretoria", "lat": -25.7479, "lng": 28.2293},
    {"name": "South Sudan", "capital": "Juba", "lat": 4.8594, "lng": 31.5713},
    {"name": "Sudan", "capital": "Khartoum", "lat": 15.5007, "lng": 32.5599},
    {"name": "Tanzania", "capital": "Dodoma", "lat": -6.1630, "lng": 35.7516},
    {"name": "Togo", "capital": "Lomé", "lat": 6.1256, "lng": 1.2254},
    {"name": "Tunisia", "capital": "Tunis", "lat": 36.8065, "lng": 10.1815},
    {"name": "Uganda", "capital": "Kampala", "lat": 0.3476, "lng": 32.5825},
    {"name": "Zambia", "capital": "Lusaka", "lat": -15.3875, "lng": 28.3228},
    {"name": "Zimbabwe", "capital": "Harare", "lat": -17.8252, "lng": 31.0335},
]

# Major non-capital African cities used as distractors
DISTRACTOR_CITIES = [
    "Lagos",
    "Casablanca",
    "Johannesburg",
    "Dar es Salaam",
    "Alexandria",
    "Durban",
    "Mombasa",
    "Douala",
    "Kumasi",
    "Fez",
    "Marrakech",
    "Ibadan",
    "Kano",
    "Port Harcourt",
    "Arusha",
    "Mwanza",
    "Benghazi",
    "Oran",
    "Blantyre",
    "Mbuji-Mayi",
    "Lubumbashi",
    "Kisumu",
    "Tangier",
    "Bulawayo",
    "Zanzibar City",
    "Timbuktu",
    "Touba",
    "Cape Town",
    "Meknes",
    "Pointe-Noire",
    "Ouarzazate",
    "Lamu",
    "Dire Dawa",
    "Benguela",
    "Nakuru",
    "Eldoret",
    "Tamale",
    "Sekondi-Takoradi",
    "Giza",
    "Suez",
]


def scrape_from_wikipedia():
    """Attempt to scrape capital data from Wikipedia."""
    url = "https://en.wikipedia.org/wiki/List_of_national_capitals_by_latitude"

    african_countries = {c["name"].lower(): c for c in FALLBACK_COUNTRIES}

    try:
        resp = requests.get(url, timeout=15, headers={
            "User-Agent": "AfricaQuizScraper/1.0 (educational project)"
        })
        resp.raise_for_status()
        soup = BeautifulSoup(resp.text, "lxml")

        tables = soup.find_all("table", class_="wikitable")
        if not tables:
            print("No wikitable found, using fallback", file=sys.stderr)
            return None

        scraped = {}
        for table in tables:
            rows = table.find_all("tr")
            for row in rows[1:]:
                cells = row.find_all("td")
                if len(cells) < 3:
                    continue

                capital_text = cells[0].get_text(strip=True)
                country_text = cells[1].get_text(strip=True)

                country_lower = country_text.lower()

                # Check if this country is in our African list
                for name_key, data in african_countries.items():
                    if name_key in country_lower or country_lower in name_key:
                        # Try to extract coordinates from the row
                        coord_cell = None
                        for cell in cells:
                            text = cell.get_text(strip=True)
                            if "°" in text and ("N" in text or "S" in text):
                                coord_cell = text
                                break

                        if capital_text and country_text:
                            scraped[name_key] = {
                                "name": data["name"],
                                "capital": capital_text.split("[")[0].strip(),
                                "lat": data["lat"],
                                "lng": data["lng"],
                            }
                        break

        if len(scraped) >= 40:
            # Fill in any missing with fallback
            result = []
            for key, data in african_countries.items():
                if key in scraped:
                    result.append(scraped[key])
                else:
                    result.append(data)
            return result
        else:
            print(f"Only scraped {len(scraped)} countries, using fallback", file=sys.stderr)
            return None

    except Exception as e:
        print(f"Scraping failed: {e}, using fallback", file=sys.stderr)
        return None


def validate_data(countries, distractors):
    """Validate that no distractor matches a capital name."""
    capitals = {c["capital"].lower() for c in countries}
    distractor_set = {d.lower() for d in distractors}

    overlap = capitals & distractor_set
    if overlap:
        print(f"WARNING: Overlap between capitals and distractors: {overlap}", file=sys.stderr)
        # Remove overlapping distractors
        distractors = [d for d in distractors if d.lower() not in capitals]

    return distractors


def main():
    # Try scraping first, fall back to hardcoded data
    countries = None
    if HAS_DEPS:
        print("Attempting to scrape from Wikipedia...")
        countries = scrape_from_wikipedia()

    if countries is None:
        print("Using hardcoded fallback data")
        countries = FALLBACK_COUNTRIES

    distractors = validate_data(countries, DISTRACTOR_CITIES)

    output = {
        "countries": countries,
        "distractors": distractors,
    }

    # Determine output path
    script_dir = os.path.dirname(os.path.abspath(__file__))
    output_path = os.path.join(
        script_dir, "..", "app", "src", "main", "assets", "african_capitals.json"
    )
    output_path = os.path.normpath(output_path)

    os.makedirs(os.path.dirname(output_path), exist_ok=True)

    with open(output_path, "w", encoding="utf-8") as f:
        json.dump(output, f, indent=2, ensure_ascii=False)

    print(f"Wrote {len(countries)} countries and {len(distractors)} distractors to {output_path}")

    # Validation summary
    assert len(countries) == 54, f"Expected 54 countries, got {len(countries)}"
    for c in countries:
        assert c["name"], f"Missing name: {c}"
        assert c["capital"], f"Missing capital: {c}"
        assert -90 <= c["lat"] <= 90, f"Invalid lat for {c['name']}: {c['lat']}"
        assert -180 <= c["lng"] <= 180, f"Invalid lng for {c['name']}: {c['lng']}"

    print("All validations passed!")


if __name__ == "__main__":
    main()
