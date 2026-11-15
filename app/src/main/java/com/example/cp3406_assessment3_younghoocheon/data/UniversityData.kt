package com.example.cp3406_assessment3_younghoocheon.data

import androidx.annotation.DrawableRes
import com.example.cp3406_assessment3_younghoocheon.R

/** A member of the IT teaching and research staff shown in the faculty directory. */
data class StaffMember(
    val id: String,
    val name: String,
    val title: String,
    @DrawableRes val photo: Int,
    val qualifications: List<String>,
    val expertise: List<String>,
    val summary: String,
    val highlights: List<String>,
    val publications: List<String>,
    val categories: List<String>,
)

data class ProgramSection(val heading: String, val points: List<String>)

data class ProgramInfo(
    val id: String,
    val name: String,
    val level: String,
    val summary: String,
    val sections: List<ProgramSection>,
    val accreditation: String?,
)

data class CampusActivity(val title: String, val description: String)

/**
 * Static content summarised from the JCU Singapore Information Technology booklet (version SIN07/26).
 * Descriptions are shortened and reworded for the app.
 */
object UniversityData {

    const val SOURCE_NOTE = "Source: James Cook University Singapore, Information Technology booklet (SIN07/26)."

    val researchAreas = listOf("All", "Security", "IoT", "AI & Data", "Games", "Health", "Maths")

    val departmentIntro =
        "Information Technology research at JCU Singapore is interdisciplinary. It covers smart systems, " +
                "secure and resilient digital infrastructure, data-driven decision making and intelligent " +
                "learning systems, with a focus on real-world challenges in tropical contexts."

    val departmentAreas = listOf(
        "Internet of Things (IoT)",
        "Cybersecurity",
        "Artificial intelligence and machine learning",
        "Data science",
        "Game design",
    )

    val professionalBodies = listOf(
        "Institute of Electrical and Electronics Engineers (IEEE)",
        "Singapore Computer Society (SCS)",
    )

    val activities = listOf(
        CampusActivity(
            "Industry projects",
            "Parts of the curriculum have students work with industry on real-world projects.",
        ),
        CampusActivity(
            "Guest lectures",
            "Experts from industry and academia visit to connect study topics with practice.",
        ),
        CampusActivity(
            "Game Jams and Hackathons",
            "Events where students from other universities also take part, compete and network.",
        ),
        CampusActivity(
            "Design Sprint",
            "A two-day event at the end of term where mixed teams solve a problem posed by industry.",
        ),
    )

    val programs = listOf(
        ProgramInfo(
            id = "bit",
            name = "Bachelor of Information Technology",
            level = "Undergraduate",
            summary = "Builds the knowledge and skills needed for professional IT careers, from " +
                    "programming and design thinking to games, data and mobile technologies.",
            sections = listOf(
                ProgramSection(
                    "Subject areas",
                    listOf(
                        "Interactive visualisation and games",
                        "Big data and data mining",
                        "Interactive 2D/3D graphics and web technologies",
                        "Mobile technologies",
                        "Programming and design thinking",
                    ),
                ),
                ProgramSection(
                    "Graduates will be able to",
                    listOf(
                        "Show essential knowledge for technology related professions",
                        "Synthesise industry standards and principles for decision making",
                        "Critically evaluate data and resources against academic literature",
                        "Apply critical thinking to IT problems",
                        "Work collaboratively",
                    ),
                ),
            ),
            accreditation = "Accredited by the Australian Computer Society (ACS). Graduates can apply to join the ACS.",
        ),
        ProgramInfo(
            id = "mit",
            name = "Master of Information Technology",
            level = "Postgraduate",
            summary = "Gives graduates of other disciplines practical skills to move into the IT industry, " +
                    "mixing technology with management and business operation skills.",
            sections = listOf(
                ProgramSection(
                    "Specialisations",
                    listOf(
                        "Business Informatics",
                        "Computing and Networking",
                        "Interactive Technologies and Games Design",
                    ),
                ),
                ProgramSection(
                    "Why it matters",
                    listOf(
                        "Growing global demand in e-Business and e-Commerce",
                        "Skills across marketing, management, multimedia, networking and software technologies",
                    ),
                ),
            ),
            accreditation = "Fully accredited by the Australian Computer Society at the Professional Level, its highest level.",
        ),
        ProgramInfo(
            id = "phd",
            name = "PhD and Master of Philosophy",
            level = "Research",
            summary = "Supervised original research that leads to a thesis showing critical analysis and a " +
                    "significant contribution to knowledge in the field.",
            sections = listOf(
                ProgramSection(
                    "How the degrees differ",
                    listOf(
                        "PhD: a supervised original research program ending in a thesis",
                        "Master of Philosophy: research supervision on a smaller scale, and a possible pathway to a PhD",
                    ),
                ),
                ProgramSection(
                    "Get in touch",
                    listOf("Research enquiries can be sent to researchsupport-singapore@jcu.edu.au"),
                ),
            ),
            accreditation = null,
        ),
    )

    val staff = listOf(
        StaffMember(
            id = "dillon",
            name = "Dr Roberto Dillon",
            title = "Associate Professor, Information Technology",
            photo = R.drawable.staff_dillon,
            qualifications = listOf(
                "PhD, Electrical and Computer Engineering, University of Genoa",
                "MEng, Electrical and Electronics Engineering, University of Genoa",
                "MicroMasters in Cybersecurity, Rochester Institute of Technology",
                "Certificate in Higher Education Teaching, Harvard University",
            ),
            expertise = listOf(
                "Emotional AI and Kansei Engineering",
                "Behavioral cybersecurity",
                "Computer security and privacy",
                "Ludology",
                "History of technology",
            ),
            summary = "Based in Singapore since 2005, Dr Dillon works on serious games, emotional AI and " +
                    "behavioural analytics for cybersecurity. He founded the first permanent Museum of Video and " +
                    "Computer Games in Southeast Asia in 2013 and helped launch a dedicated Cybersecurity degree in 2020.",
            highlights = listOf(
                "Created the 6-11 Framework for game analysis, now used in university curricula",
                "Experimental games shown at the Tokyo Game Show and FILE Games in Rio de Janeiro",
                "Senior Member of IEEE and Professional Member of (ISC)2",
            ),
            publications = listOf(
                "The Cybersecurity Game Master (CRC Press, 2026)",
                "Behavioral Biometrics for Remote Exam Integrity (2025)",
                "On the Way to Fun (AKPeters/CRC Press, 2010)",
            ),
            categories = listOf("Security", "Games", "AI & Data"),
        ),
        StaffMember(
            id = "jusak",
            name = "Dr Jusak Jusak",
            title = "Senior Lecturer, Internet of Things",
            photo = R.drawable.staff_jusak,
            qualifications = listOf(
                "PhD, Communications Engineering, RMIT University",
                "B.Eng, Electronics Engineering, Brawijaya University",
                "MIEEE, FHEA",
            ),
            expertise = listOf(
                "IoT systems and architecture",
                "IoT for healthcare systems",
                "IoT with distributed ledgers such as blockchain",
                "Machine learning with IoT",
            ),
            summary = "Dr Jusak designs IoT systems and devices for monitoring and tele-healthcare. His work also " +
                    "covers lightweight blockchain for healthcare IoT and TinyML, which brings machine learning to " +
                    "small, resource-limited devices.",
            highlights = listOf(
                "Best Paper Award at an information technology conference in 2018",
                "Futuristic Technology Award in 2022 for IoT devices that measure inland water bodies",
                "Award for Excellence and Leadership at JCU Singapore in 2025",
                "Contributes to an IEEE standard on 6G for robotics",
            ),
            publications = listOf(
                "Integration of IoT and Distributed Ledger Technologies (ACM Computing Surveys, 2026)",
                "Security and Privacy for Modern Wireless Communication Systems (Electronics, 2025)",
            ),
            categories = listOf("IoT", "Health"),
        ),
        StaffMember(
            id = "song",
            name = "Dr Insu Song",
            title = "Senior Lecturer, Information Technology",
            photo = R.drawable.staff_song,
            qualifications = listOf(
                "PhD, Computer Science, University of Queensland",
                "BInfoTech (Hons I), Griffith University",
                "BSc, Physics, Chung-Ang University",
            ),
            expertise = listOf(
                "Information systems design",
                "Embedded system design",
                "Industrial control system design",
                "Electronics engineering",
                "Signal processing",
                "Health informatics",
            ),
            summary = "Dr Song has more than 15 years of industry experience and over 60 peer reviewed papers. His " +
                    "interests include biomedical engineering, health and mental health informatics, knowledge " +
                    "engineering and text mining.",
            highlights = listOf(
                "2011 Gates Foundation Grand Challenges grant for a mobile app that supports early child " +
                        "health intervention using breathing sound",
            ),
            publications = listOf(
                "Diagnosis of pneumonia from sounds collected using low cost cell phones (IJCNN, 2015)",
                "Fast diagnosis of bowel activities (IJCNN, 2017)",
            ),
            categories = listOf("IoT", "Health"),
        ),
        StaffMember(
            id = "zhu",
            name = "Dr Randy Zhu",
            title = "Lecturer, School of Science and Technology",
            photo = R.drawable.staff_zhu,
            qualifications = listOf(
                "PhD, Information Technology, James Cook University",
                "MSc, Information Systems, Nanyang Technological University",
                "BSc, Computer Science, University of Adelaide",
            ),
            expertise = listOf(
                "Ethical and responsible AI",
                "Human-centred AI design",
                "Participatory action research",
                "Human-AI interaction",
                "Digital inclusion",
            ),
            summary = "Dr Zhu studies human-computer interaction with a focus on accessibility and inclusion. He " +
                    "co-designs technology with end-users, caregivers and communities so that the result is usable " +
                    "and meets real needs.",
            highlights = listOf(
                "Built a framework for engaging adolescents with autism in research through a support group",
                "Ethnographic study of how group culture shapes co-design",
            ),
            publications = listOf(
                "Community led co-design of a social networking platform with adolescents with Autism Spectrum Disorder (2022)",
                "An IoT conceptual model and user experience design for forest hiking systems (2023)",
            ),
            categories = listOf("AI & Data"),
        ),
        StaffMember(
            id = "kerrison",
            name = "Dr Steve Kerrison",
            title = "Senior Lecturer, Cybersecurity",
            photo = R.drawable.staff_kerrison,
            qualifications = listOf(
                "PhD, Computer Science, University of Bristol",
                "MEng, Computer Systems Engineering, University of Bristol",
                "SFHEA, CISSP, CCSP",
            ),
            expertise = listOf(
                "AI-assisted cybersecurity",
                "Multi-core embedded systems",
                "Innovation in teaching technologies",
                "Cybersecurity for IoT",
                "Public key infrastructure",
            ),
            summary = "Dr Kerrison works on making cybersecurity easy, efficient and effective, from cloud systems " +
                    "down to chip design. His research includes showing how hacked smart padlocks can steal " +
                    "fingerprints and how to defend against such IoT threats.",
            highlights = listOf(
                "A patent he co-authored is used in products that secure IoT communications",
                "Created a 480-core real-time embedded system and shared the designs as open source",
                "Formally verified a time-deterministic communication model for multi-core systems",
            ),
            publications = listOf(
                "IoT Droplocks: Wireless fingerprint theft using hacked smart locks (2022)",
                "Teaching the Internet with AR and Toy Bricks (TENCON, 2024)",
            ),
            categories = listOf("Security", "IoT"),
        ),
        StaffMember(
            id = "sharifonnasabi",
            name = "Dr Ghazalnaz Sharifonnasabi",
            title = "Lecturer, Information Technology",
            photo = R.drawable.staff_sharifonnasabi,
            qualifications = listOf(
                "PhD, Computer Science, Taylor University",
                "MA (Fine Arts), Science Technology and Sustainability, University of Malaysia",
                "BEd (Arts/Crafts), Business Information Technology, Staffordshire University",
            ),
            expertise = listOf(
                "Artificial intelligence",
                "Machine learning and deep learning",
                "Computer vision",
                "Medical image analysis",
                "Healthcare analytics",
                "Smart tourism technologies",
            ),
            summary = "Dr Sharifonnasabi applies AI to healthcare, sustainability and digital innovation. She " +
                    "developed DentixAI, a system that estimates biological age from dental panoramic radiographs, " +
                    "and co-leads SleepSync, which explores AI support for healthier sleep habits.",
            highlights = listOf(
                "DentixAI supports uses in forensic science, immigration and sports governance",
                "Research on intelligent fish detection for aquaculture and AI-based crop disease analysis",
            ),
            publications = listOf(
                "Hybrid HCNN-KNN model enhances age estimation accuracy in orthopantomography (2022)",
                "Halal servicescape in the metaverse (2024)",
            ),
            categories = listOf("AI & Data", "Health"),
        ),
        StaffMember(
            id = "hormann",
            name = "Dr Erik Hormann",
            title = "Lecturer, Mathematics",
            photo = R.drawable.staff_hormann,
            qualifications = listOf(
                "DPhil, Mathematics, University of Oxford",
                "Bachelor's and Master's degrees, La Sapienza University of Rome",
                "MIMA, FHEA",
            ),
            expertise = listOf("Network science", "Complex systems"),
            summary = "Dr Hormann uses physics-inspired methods to study complex systems and networks. His work looks " +
                    "at how groups reach agreement on hypergraphs, how hard it is to detect communities in sparse " +
                    "networks, and how to simplify large directed graphs.",
            highlights = listOf(
                "Previously taught at the University of Edinburgh",
                "DPhil supervised by Professor Renaud Lambiotte at Oxford",
            ),
            publications = listOf(
                "Approximation for return time distributions of random walks on sparse networks (2025)",
                "Consensus from group interactions: an adaptive voter model on hypergraphs (2022)",
            ),
            categories = listOf("Maths"),
        ),
        StaffMember(
            id = "shen",
            name = "Dr Lijuan Shen",
            title = "Senior Lecturer, School of Science and Technology",
            photo = R.drawable.staff_shen,
            qualifications = listOf(
                "PhD, Systems Engineering, Beihang University",
                "BSc, Information and Computing Science, Beijing Jiaotong University",
            ),
            expertise = listOf(
                "Systems and infrastructure resilience",
                "Reliability engineering",
                "Predictive maintenance",
                "Fault detection and diagnostics",
                "Statistical modelling and machine learning",
            ),
            summary = "Dr Shen develops data-driven methods that improve how complex infrastructure such as energy " +
                    "and transport systems copes with wear, disruption and recovery. She has published over 40 " +
                    "peer-reviewed journal articles.",
            highlights = listOf(
                "Resilience assessment frameworks for power and transport systems",
                "Methods that predict failures earlier so maintenance can be planned better",
            ),
            publications = listOf(
                "Priority assignment of repair services for spatially distributed infrastructure (IISE Transactions, 2026)",
                "Understanding key factors affecting power systems resilience (2021)",
            ),
            categories = listOf("Maths", "AI & Data"),
        ),
    )

    fun staffById(id: String): StaffMember? = staff.firstOrNull { it.id == id }

    fun programById(id: String): ProgramInfo? = programs.firstOrNull { it.id == id }
}