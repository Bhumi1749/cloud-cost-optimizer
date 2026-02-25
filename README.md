# AI-Based Cloud Cost Optimization System with Decision Impact Simulator

A production-grade Java Spring Boot application that uses Machine Learning to optimize cloud infrastructure costs and provides AI-powered decision impact simulation before applying changes.

## 🎯 Project Overview

This system analyzes cloud Virtual Machine (VM) usage patterns using K-Means clustering and provides intelligent cost optimization recommendations with risk assessment. It includes a unique **Decision Impact Simulator** that predicts the consequences of optimization actions before execution.

## 🚀 Key Features

### Phase 1: Core Cost Optimization
- **ML-Powered Analysis**: Uses Weka K-Means clustering to classify VMs into Under-utilized, Optimal, and Over-utilized categories
- **Cost Savings Calculation**: Automatically calculates potential monthly savings per VM
- **Real-time Dashboard**: Professional web interface with live data visualization
- **H2 Database**: Persistent storage for VM data and analysis results

### Phase 2: Decision Impact Simulator (Unique Feature)
- **What-If Analysis**: Simulate 4 different actions before applying:
  - Downscale (50% resource reduction)
  - Upscale (200% resource increase)
  - Schedule (run only during business hours)
  - Terminate (complete shutdown)
- **Risk Assessment**: AI calculates risk scores (0-100) based on multiple factors
- **Performance Impact Prediction**: Estimates performance changes
- **Cost Impact Prediction**: Forecasts exact monthly cost changes
- **Explainable AI**: Provides human-readable reasoning for recommendations
- **Confidence Scoring**: Shows AI confidence level (0-100%)
- **Smart Recommendations**: 
  - SAFE_TO_APPLY (Low risk, good savings)
  - APPLY_WITH_CAUTION (Medium risk, monitor required)
  - NOT_RECOMMENDED (High risk, consider alternatives)

### Phase 3: Visual Analytics
- **Interactive Charts**: 
  - Pie Chart: VM distribution by cluster
  - Bar Chart: Cost analysis with savings comparison
  - Line Chart: Resource utilization trends (CPU, RAM, Disk)
- **Real-time Updates**: Charts update dynamically with data

### Phase 4: Export & Reporting
- **Excel Export**: Detailed spreadsheet with all VM metrics
- **PDF Export**: Professional reports with summary statistics
- **Custom Formatting**: Color-coded clusters and styled headers

### Phase 5: File Management
- **CSV Upload**: Direct file upload from browser
- **Format Validation**: Ensures only valid CSV files are processed
- **Automatic Analysis**: Uploaded files are instantly analyzed

### Phase 6: Security & Authentication
- **User Authentication**: Spring Security with BCrypt encryption
- **Session Management**: Secure login/logout functionality
- **Protected Endpoints**: Role-based access control

## 🏗️ Architecture

### Technology Stack
- **Backend**: Java 17, Spring Boot 3.1.5
- **Security**: Spring Security with BCrypt
- **Database**: H2 (In-memory for development) / MySQL (for production)
- **Machine Learning**: Weka 3.8.6 (K-Means clustering)
- **CSV Processing**: OpenCSV 5.8
- **Export**: Apache POI (Excel), iText (PDF)
- **Frontend**: HTML5, CSS3, JavaScript (ES6), Chart.js
- **Template Engine**: Thymeleaf

### Project Structure
```
src/main/java/com/cloudcost/optimizer/
├── controller/
│   ├── VMController.java          # REST API endpoints
│   ├── DashboardController.java   # Web page controller
│   └── AuthController.java        # Authentication controller
├── service/
│   ├── VMAnalysisService.java     # Core analysis logic
│   ├── MLService.java             # Machine Learning operations
│   ├── DecisionImpactService.java # Decision simulation engine
│   ├── ExportService.java         # Excel/PDF generation
│   └── UserService.java           # User management
├── model/
│   ├── VirtualMachine.java        # VM entity
│   ├── DecisionScenario.java      # Simulation result entity
│   └── User.java                  # User entity
├── repository/
│   ├── VirtualMachineRepository.java
│   ├── DecisionScenarioRepository.java
│   └── UserRepository.java
├── config/
│   └── SecurityConfig.java        # Spring Security configuration
└── util/
    └── CSVReaderUtil.java         # CSV parsing utility

src/main/resources/
├── static/
│   ├── css/style.css              # Professional UI styling
│   └── js/app.js                  # Frontend logic + Chart.js
├── templates/
│   ├── dashboard.html             # Main dashboard
│   ├── login.html                 # Login page
│   └── register.html              # Registration page
└── application.properties         # Configuration
```

## 📊 Machine Learning Algorithm

### K-Means Clustering Logic
The system uses 3 clusters to categorize VMs:

**Cluster 0 (Under-utilized)**
- Average usage < 30%
- Recommendation: Downscale or terminate
- Potential savings: 50-100% of current cost

**Cluster 1 (Optimal)**
- Average usage 30-75%
- Recommendation: Keep current configuration
- Minimal changes needed

**Cluster 2 (Over-utilized)**
- Average usage > 75%
- Recommendation: Upscale resources
- Investment needed for performance

### Decision Impact Simulation Algorithm

#### Risk Scoring Formula
```
Risk Score = Utilization Factor + Performance Impact Factor + Cost-Performance Tradeoff Factor

Where:
- Utilization Factor (0-40):
  - High usage + Downscale = 40 points
  - Medium usage + Downscale = 20 points
  - Low usage + Downscale = 5 points

- Performance Impact Factor (0-35):
  - >50% performance loss = 35 points
  - 20-50% performance loss = 15 points
  - <20% performance loss = 5 points

- Cost-Performance Tradeoff (0-20):
  - Increasing cost AND reducing performance = 20 points
  - Otherwise = 0 points

Total Risk Score capped at 100
```

#### Risk Levels
- **LOW**: Risk Score < 30
- **MEDIUM**: Risk Score 30-60
- **HIGH**: Risk Score > 60

#### Confidence Score
```
Confidence = 100 - Risk Score
```

## 🔧 Installation & Setup

### Prerequisites
- Java 17+
- Maven 3.6+
- Eclipse IDE or IntelliJ IDEA

### Steps
1. Clone the repository
```bash
git clone https://github.com/Bhumi1749/cloud-cost-optimizer.git
cd cloud-cost-optimizer
```

2. Build the project
```bash
mvn clean install
```

3. Run the application
```bash
mvn spring-boot:run
```

4. Access the application
```
http://localhost:8080
```

## 🔐 Authentication

The system includes secure user authentication:

### First Time Setup
1. Navigate to `http://localhost:8080`
2. Click **"Register here"**
3. Create an account with:
   - Username
   - Email
   - Password (minimum 6 characters)
4. Login with your credentials

### Features
- **Spring Security** with BCrypt password encryption
- **Session management** with automatic logout
- **Role-based access control** (ROLE_USER)
- **Protected endpoints** - authentication required for dashboard access

### Default Access
- Login page: `http://localhost:8080/login`
- Register page: `http://localhost:8080/register`
- Dashboard (after login): `http://localhost:8080/dashboard`

## 📖 Usage Guide

### 1. Analyze VMs
- Click "📤 Upload & Analyze CSV" to upload your VM data file
- Or use the sample CSV provided in `data/vm_usage_data.csv`

### 2. View Results
- Dashboard shows summary statistics
- Charts visualize distribution and trends
- Table displays detailed VM metrics

### 3. Simulate Decisions
- Click "🎯 Simulate" on any VM
- Choose an action (Downscale, Upscale, Schedule, Terminate)
- Review predicted impact, risk, and AI reasoning
- Decide whether to apply the change

### 4. Export Reports
- Click "📊 Export Excel" for spreadsheet
- Click "📄 Export PDF" for formatted report

## 📈 Sample CSV Format
```csv
vmId,cpuUsage,ramUsage,diskUsage,costPerHour
VM-001,15.5,20.3,35.2,0.50
VM-002,85.2,78.5,65.3,2.00
VM-003,25.8,30.2,40.5,0.75
```

## 🎓 Learning Outcomes

### Technical Skills Demonstrated
- RESTful API design
- Machine Learning integration
- Database modeling (JPA/Hibernate)
- Spring Security implementation
- File processing and validation
- Report generation
- Frontend-backend integration
- Responsive web design

### Design Patterns Used
- MVC (Model-View-Controller)
- Repository Pattern
- Service Layer Pattern
- Dependency Injection

### Software Engineering Practices
- Clean code architecture
- Separation of concerns
- SOLID principles
- Exception handling
- Input validation
- Password encryption
- Session management

## 🚀 Future Enhancements

- [x] User authentication and authorization ✅
- [ ] MySQL/PostgreSQL support for production
- [ ] Multi-tenancy support
- [ ] Email notifications for high-risk VMs
- [ ] Historical trend analysis
- [ ] Advanced ML models (Random Forest, Neural Networks)
- [ ] Cloud provider API integration (AWS, Azure, GCP)
- [ ] Automated action execution
- [ ] Scheduling optimization recommendations
- [ ] Cost forecasting

## 📝 API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/vms/analyze` | Analyze VMs from file path |
| POST | `/api/vms/upload` | Upload and analyze CSV |
| GET | `/api/vms/all` | Get all VMs |
| DELETE | `/api/vms/clear` | Clear all VM data |
| POST | `/api/vms/simulate` | Simulate decision impact |
| GET | `/api/vms/export/excel` | Download Excel report |
| GET | `/api/vms/export/pdf` | Download PDF report |
| GET | `/login` | Login page |
| POST | `/register` | User registration |
| GET | `/dashboard` | Main dashboard (protected) |

## 👨‍💻 Author

**Bhumika Kalamkar**
- GitHub: [github.com/Bhumi1749](https://github.com/Bhumi1749)
- LinkedIn: [www.linkedin.com/in/bhumika-kalamkar]
- Email: [bhumikakalamkar1749@gmail.com]

## 📄 License

This project is licensed under the MIT License.

## 🙏 Acknowledgments

- Weka Machine Learning Library
- Spring Boot Framework
- Spring Security
- Chart.js Visualization Library
- Apache POI & iText for report generation
