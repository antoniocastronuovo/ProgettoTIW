package it.polimi.tiw.beans;

public class ExamResult {
	private Student student;
	private ExamSession examSession;
	private int grade;
	private boolean laude;
	private String gradeStatus;
	
	public ExamResult() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Student getStudent() {
		return student;
	}

	public void setStudent(Student student) {
		this.student = student;
	}

	public ExamSession getExamSession() {
		return examSession;
	}

	public void setExamSession(ExamSession examSession) {
		this.examSession = examSession;
	}

	public int getGrade() {
		return grade;
	}
	
	public String getGradeAsString() {
		switch (grade){
		case -4:
			return "<VUOTO>";
		case -3:
			return "ASSENTE";
		case -2:
			return "RIMANDATO";
		case -1:
			return "RIPROVATO";
		case 31:
			return "30 e lode";
		default:
			return ""+grade;				
		}
	}

	public void setGrade(int grade) {
		this.grade = grade;
	}

	public boolean isLaude() {
		return laude;
	}

	public void setLaude(boolean laude) {
		this.laude = laude;
	}

	public String getGradeStatus() {
		return gradeStatus;
	}

	public void setGradeStatus(String gradeStatus) {
		this.gradeStatus = gradeStatus;
	}
	
	public boolean isEditable() {
		return gradeStatus.equals("NON INSERITO") || gradeStatus.equals("INSERITO");
	}
	
	
}
