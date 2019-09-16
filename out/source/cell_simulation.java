import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class cell_simulation extends PApplet {

int numStartingCells = 3;
ArrayList<Cell> cells = new ArrayList<Cell>();

public void setup() {
  // size(1000, 1000);
	
  
  colorMode(HSB, 360);


  for (int i = 0; i < numStartingCells; i++) {
    Cell cell = new Cell(new Point(random(width), random(height)), random(60, 100), random(360), i);
    cells.add(cell);
  }
  noStroke();
  // fill(255, 204);
}

public void draw() {
  background(0);
	// println(cells.size());
  for (int i = 0; i < cells.size(); i++) {
    cells.get(i).update(cells);
		for (int j = i + 1; j < cells.size(); j++) {
			cells.get(i).collide(cells.get(j));
		}
  }
}
float spring = 0.05f;
float gravity = 0.03f;
float friction = -0.9f;
float growthChance = 0.1f;
float sicknessChance = 0.0005f;
float deathChance = 0.001f;
float growthStep = 0.5f;
float maxSize = 50;

class Point {
	float x, y;

	Point(float x, float y) {
		this.x = x;
		this.y = y;
	}
}

class Cell {
  Point p;
  PVector v;
  float d, dna;
  int id;
	boolean sick = false;
 
  Cell(Point p, float d, float dna, int id) {
    this.p = p;
    this.d = d;
		this.dna = dna;
    this.id = id;
    this.v = new PVector(random(0.1f, 1), random(0.1f, 1));
  }

  Cell(Point p, PVector v, float d, float dna, int id) {
    this.p = p;
		this.v = v;
    this.d = d;
		this.dna = dna;
    this.id = id;
  }

  public void update(ArrayList<Cell> cells) {
		move();
		display();

		if (!sick && random(1) < growthChance) {
			d += growthStep;
		} else if (sick) {
			if (random(0.5f) < growthChance) d -= growthStep;
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

  public void mitosis(ArrayList<Cell> cells) {
		d /= 2;
		p.x -= d;
		p.y -= d;

		Point point = new Point(p.x + d/2, p.y + d/2);
		Cell newCell = new Cell(point, v.copy(), d, mutate(), cells.size());

		cells.add(newCell);
  }

	public float mutate() {
		return min(max(0, dna + random(-20, 20)), 360);
	}

	public void die(ArrayList<Cell> cells) {
		cells.remove(this);
	}
  
	public void collide(Cell cell) {
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
  
  public void move() {
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
  
  public void display() {
		if (sick) {
			fill(dna, 300, 300);
		} else {
			fill(dna, 360, 360);
		}

    ellipse(p.x, p.y, d, d);
  }
}
  public void settings() { 	fullScreen(); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "cell_simulation" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
