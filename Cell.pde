float spring = 0.05;
float friction = -0.9;
float growthChance = 0.1;
float sicknessChance = 0.0005;
float deathChance = 0.001;
float growthStep = 0.5;
float maxSize = 50;

class Cell {
  PVector p, v;
  float d, dna;
  int id;
	boolean sick = false;
 
  Cell(float x, float y, float d, float dna, int id) {
    this.p = p;
    this.d = d;
		this.dna = dna;
    this.id = id;
    this.v = new PVector(random(0.01, MAX_VELOCITY), random(0.01, MAX_VELOCITY));
  }

  Cell(float x, float y, PVector v, float d, float dna, int id) {
    this.p = p;
		this.v = v;
    this.d = d;
		this.dna = dna;
    this.id = id;
  }

  void update(ArrayList<Cell> cells) {
		move();
		display();

		if (!sick && random(1) < growthChance) {
			d += growthStep;
		} else if (sick) {
			if (random(0.5) < growthChance) d -= growthStep;
			if (d <= 0) {
				die(cells);
			}
		}
		
		if (random(1) < sicknessChance) {
			sick = true;
		}

		if (sick && random(1) < deathChance) {
			die(cells);
		}

		if (d > maxSize) {
			mitosis(cells);
		}
  }

  void mitosis(ArrayList<Cell> cells) {
		d /= 2;
		p.x -= d;
		p.y -= d;

		Point point = new Point(p.x + d/2, p.y + d/2);
		Cell newCell = new Cell(point, v.copy(), d, mutate(), cells.size());

		cells.add(newCell);
  }

	float mutate() {
		return min(max(0, dna + random(-20, 20)), 360);
	}

	void die(ArrayList<Cell> cells) {
		cells.remove(this);
	}
  
	void collide(Cell cell) {
		float dx = cell.p.x - p.x;
		float dy = cell.p.y - p.y;

		float distance = sqrt(dx*dx + dy*dy);

		float minDist = cell.d/2 + d/2;

		if (distance < minDist) { 
			float angle = atan2(dy, dx);
			float targetX = p.x + cos(angle) * minDist;
			float targetY = p.y + sin(angle) * minDist;
			float ax = (targetX - cell.p.x) * spring;
			float ay = (targetY - cell.p.y) * spring;
			v.x -= ax;
			v.y -= ay;
			cell.v.x += ax;
			cell.v.y += ay;

			if (sick) {
				if (Math.abs(cell.dna - dna) < 30) {
					cell.sick = true;
				}
			}
		}
  }
  
  void move() {
    p.x += v.x;
    p.y += v.y;
    if (p.x + d/2 > width) {
      p.x = width - d/2;
      v.x *= friction; 
    }
    else if (p.x - d/2 < 0) {
      p.x = d/2;
      v.x *= friction;
    }
    if (p.y + d/2 > height) {
      p.y = height - d/2;
      v.y *= friction; 
    } 
    else if (p.y - d/2 < 0) {
      p.y = d/2;
      v.y *= friction;
    }
  }
  
  void display() {
		if (sick) {
			fill(dna, 300, 300);
		} else {
			fill(dna, 360, 360);
		}

    ellipse(p.x, p.y, d, d);
  }
}
