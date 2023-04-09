import itertools
import csv

def manhattan_distance(state, fixed_tiles):
    distance = 0
    for idx, value in enumerate(state):
        if value != 0 and value in fixed_tiles:
            goal_idx = fixed_tiles.index(value)
            distance += abs(idx // 4 - goal_idx // 4) + abs(idx % 4 - goal_idx % 4)
    return distance

def generate_pattern_database(fixed_tiles):
    pattern_data = {}
    unfixed_tiles = tuple(i for i in range(1, 17) if i not in fixed_tiles)

    for perm in itertools.permutations(unfixed_tiles):
        state = list(perm)
        for fixed_value in fixed_tiles:
            state.insert(fixed_tiles.index(fixed_value), fixed_value)
        pattern_data[perm] = manhattan_distance(state, fixed_tiles)

    return pattern_data

def write_pattern_data_to_csv(pattern_data, file_name):
    with open(file_name, 'w', newline='') as csvfile:
        writer = csv.writer(csvfile)
        for key, value in pattern_data.items():
            writer.writerow(list(key) + [value])

fixed_tiles1 = (1, 2, 3, 4, 5, 6, 7, 8)
pattern_data1 = generate_pattern_database(fixed_tiles1)
write_pattern_data_to_csv(pattern_data1, 'patterns1.csv')

fixed_tiles2 = (9, 10, 11, 12, 13, 14, 15)
pattern_data2 = generate_pattern_database(fixed_tiles2)
write_pattern_data_to_csv(pattern_data2, 'patterns2.csv')
print(1)