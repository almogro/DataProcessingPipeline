#!/usr/bin/env python3
"""
Script to generate sample movie data for the functional programming project.
This generates 10,000+ records as required by the project specifications.
"""

import random
import csv

# Sample data for generating realistic movie records
GENRES = [
    "Action", "Adventure", "Animation", "Biography", "Comedy", "Crime", "Documentary",
    "Drama", "Family", "Fantasy", "Film-Noir", "History", "Horror", "Music", "Musical",
    "Mystery", "Romance", "Sci-Fi", "Sport", "Thriller", "War", "Western"
]

SAMPLE_TITLES = [
    "The", "A", "An", "In", "On", "At", "For", "With", "Without", "Beyond",
    "Adventure", "Mystery", "Journey", "Quest", "Story", "Tale", "Legend",
    "Hero", "Warrior", "Knight", "Princess", "King", "Queen", "Prince",
    "City", "Mountain", "River", "Ocean", "Forest", "Desert", "Sky",
    "Love", "Hate", "Fear", "Hope", "Dream", "Night", "Day", "Sun", "Moon"
]

SAMPLE_WORDS = [
    "Redemption", "Awakening", "Rebellion", "Revolution", "Freedom", "Justice",
    "Truth", "Beauty", "Power", "Strength", "Courage", "Honor", "Glory",
    "Victory", "Defeat", "Triumph", "Destiny", "Fate", "Chance", "Luck",
    "Magic", "Mystery", "Secret", "Hidden", "Lost", "Found", "Discovery",
    "Journey", "Adventure", "Quest", "Mission", "Purpose", "Meaning"
]

def generate_movie_title():
    """Generate a random movie title."""
    title_parts = []
    
    # 70% chance of starting with "The", "A", or "An"
    if random.random() < 0.7:
        title_parts.append(random.choice(SAMPLE_TITLES[:3]))
    
    # Add 1-3 descriptive words
    num_words = random.randint(1, 3)
    for _ in range(num_words):
        title_parts.append(random.choice(SAMPLE_WORDS))
    
    return " ".join(title_parts)

def generate_movie_record(movie_id):
    """Generate a single movie record."""
    title = generate_movie_title()
    year = random.randint(1920, 2024)
    genre = random.choice(GENRES)
    
    # Generate rating with some bias towards higher ratings for popular movies
    base_rating = random.uniform(1.0, 10.0)
    # Add some bias based on year (newer movies might have different rating patterns)
    if year > 2000:
        base_rating = max(1.0, min(10.0, base_rating + random.uniform(-0.5, 1.0)))
    
    rating = round(base_rating, 1)
    
    # Generate votes with some correlation to rating
    base_votes = random.randint(100, 5000000)
    if rating > 8.0:
        base_votes = int(base_votes * random.uniform(1.5, 3.0))
    elif rating < 4.0:
        base_votes = int(base_votes * random.uniform(0.1, 0.5))
    
    votes = max(0, base_votes)
    
    return {
        'id': str(movie_id),
        'title': title,
        'year': year,
        'genre': genre,
        'rating': rating,
        'votes': votes
    }

def main():
    """Generate the dataset."""
    print("Generating movie dataset...")
    
    # Generate 10,000+ records
    num_records = 10000
    
    with open('data/input/movies_large.csv', 'w', newline='', encoding='utf-8') as csvfile:
        fieldnames = ['id', 'title', 'year', 'genre', 'rating', 'votes']
        writer = csv.DictWriter(csvfile, fieldnames=fieldnames)
        
        writer.writeheader()
        
        for i in range(1, num_records + 1):
            record = generate_movie_record(i)
            writer.writerow(record)
            
            if i % 1000 == 0:
                print(f"Generated {i} records...")
    
    print(f"Dataset generation complete! Generated {num_records} movie records.")
    print("File saved as: data/input/movies_large.csv")

if __name__ == "__main__":
    main()
