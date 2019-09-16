List<Cell> cells = new ArrayList<Cell>();
int STARTING_CELLS = 3;

void setup() {
  // size(1000, 1000);
	fullScreen();
  
  colorMode(HSB, 360);


  for (int i = 0; i < STARTING_CELLS; i++) {
    Cell cell = new Cell(random(width), random(height), random(60, 100), random(360), i);
    cells.add(cell);
  }
  noStroke();
  // fill(255, 204);
}

void draw() {
  background(0);
	// println(cells.size());
  for (int i = 0; i < cells.size(); i++) {
    cells.get(i).update(cells);
		for (int j = i + 1; j < cells.size(); j++) {
			cells.get(i).collide(cells.get(j));
		}
  }
}