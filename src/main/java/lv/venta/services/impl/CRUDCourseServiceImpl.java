package lv.venta.services.impl;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lv.venta.models.Course;
import lv.venta.models.Grade;
import lv.venta.models.Professor;
import lv.venta.repos.ICourseRepo;
import lv.venta.repos.IGradeRepo;
import lv.venta.repos.IProfessorRepo;
import lv.venta.services.ICRUDCourseService;

@Service
public class CRUDCourseServiceImpl implements ICRUDCourseService{
	
	@Autowired
	private ICourseRepo courseRepo;
	
	@Autowired
	private IProfessorRepo profRepo;
	
	@Autowired
	private IGradeRepo gradeRepo;

	@Override
	public ArrayList<Course> retrieveAllCourses() {
		return (ArrayList<Course>) courseRepo.findAll();
	}

	@Override
	public Course retrieveOneCourseById(long id) throws Exception {
		if(courseRepo.existsById(id))
			return courseRepo.findById(id).get();
		else
			throw new Exception("Incorrect id");
	}

	@Override
	public ArrayList<Course> retrieveAllCoursesByTitle(String title) throws Exception {
		ArrayList<Course> resultCourses = courseRepo.findByTitle(title);
		if(resultCourses != null)
			return resultCourses;
		else
			throw new Exception("Incorrect title");
	}

	@Override
	public Course insertCourseByParams(String title, int creditpoints, ArrayList<Professor> professors) {
		Course course = new Course(title, creditpoints, professors);
		return courseRepo.save(course);
	}

	@Override
	public Course updateCourseByParams(long id, String title, int creditpoints, ArrayList<Professor> professors)
			throws Exception {
		if(courseRepo.existsById(id))
		{
			Course course =  courseRepo.findById(id).get();
			course.setTitle(title);
			course.setCreditpoints(creditpoints);
			course.setProfessors(professors);
			return courseRepo.save(course);
		}
		else
			throw new Exception("Incorrect id");
	}

	@Override
	public void deleteCourseById(long id) throws Exception {
		if(courseRepo.existsById(id)) {
			Course removeCourse = courseRepo.findById(id).get();
			ArrayList<Professor> profs= profRepo.findAllByCoursesIdc(id);
			for(Professor prof: profs) {
				prof.removeCourse(removeCourse);
				profRepo.save(prof);
			}
			
			
			
			ArrayList<Grade> grade= gradeRepo.findByCourseIdc(id);
			for(Grade gr: grade) {
				gr.setCourse(null);
				gradeRepo.save(gr);
			}
			courseRepo.deleteById(id);
		}
		else
			throw new Exception("Incorrect id");
		
	}

}
