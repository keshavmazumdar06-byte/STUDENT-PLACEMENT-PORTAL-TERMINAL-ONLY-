
import java.io.*;
import java.util.*;

public class Main {
    static Scanner in = new Scanner(System.in);
    static ArrayList<Student> students = new ArrayList<>();
    static ArrayList<Company> companies = new ArrayList<>();
    static ArrayList<Job> jobs = new ArrayList<>();
    static ArrayList<Application> apps = new ArrayList<>();
    static StudentBST bst = new StudentBST();
    static StudentHashTable hash = new StudentHashTable();
    static final String FILE = "data/portal.dat";

    public static void main(String[] z) {
        load();
        reindex();
        while (true) {
            System.out.println(
                    "\n===== CAMPUS PLACEMENT PORTAL =====\n1 Students\n2 Companies\n3 Jobs\n4 Apply\n5 Eligibility\n6 Merit list (Merge Sort)\n7 Shortlist (Max Heap)\n8 DSA Lab\n9 Reports\n10 Save\n0 Save & Exit");
            int c = num("Choice: ");
            switch (c) {
                case 1 -> studentMenu();
                case 2 -> companyMenu();
                case 3 -> jobMenu();
                case 4 -> apply();
                case 5 -> eligibility();
                case 6 -> merit();
                case 7 -> shortlist();
                case 8 -> dsa();
                case 9 -> report();
                case 10 -> save();
                case 0 -> {
                    save();
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    static String ask(String p) {
        System.out.print(p);
        return in.nextLine().trim();
    }

    static int num(String p) {
        while (true)
            try {
                return Integer.parseInt(ask(p));

            } catch (Exception e) {
                System.out.println("Enter a valid integer.");

            }
    }

    static double dec(String p) {
        while (true)
            try {
                return Double.parseDouble(ask(p));
            } catch (Exception e) {
                System.out.println("Enter a valid number.");
            }
    }

    static boolean yes(String p) {
        return ask(p + " (y/n): ").equalsIgnoreCase("y");
    }

    static void studentMenu() {
        while (true) {
            System.out.println("\n-- STUDENTS --\n1 Add  2 List  3 Search ID  4 Update CGPA  5 Delete  0 Back");
            int c = num("Choice: ");
            if (c == 0)
                return;
            if (c == 1) {
                String id = ask("ID: ");
                if (hash.get(id) != null) {
                    System.out.println("ID exists.");
                    continue;
                }
                String n = ask("Name: "), b = ask("Branch: ");
                double g = dec("CGPA (0-10): ");
                int bl = num("Backlogs: ");
                String sk = ask("Skills comma-separated: ");
                if (g < 0 || g > 10 || bl < 0) {
                    System.out.println("Invalid academic data.");
                    continue;
                }
                Student s = new Student(id, n, b, g, bl, sk);
                students.add(s);
                hash.put(s);
                bst.add(s);
                System.out.println("Added.");
            } else if (c == 2) {
                for (Student s : students)
                    show(s);
            } else if (c == 3) {
                Student s = hash.get(ask("ID: "));
                if (s == null)
                    System.out.println("Not found.");
                else
                    show(s);
            } else if (c == 4) {
                Student s = hash.get(ask("ID: "));
                if (s == null)
                    System.out.println("Not found.");
                else {
                    s.cgpa = dec("New CGPA: ");
                    reindex();
                    System.out.println("Updated.");
                }
            } else if (c == 5) {
                String id = ask("ID: ");
                Student s = hash.get(id);
                if (s != null && yes("Delete " + s.name + "?")) {
                    students.remove(s);
                    apps.removeIf(a -> a.sid.equals(id));
                    reindex();
                    System.out.println("Deleted.");
                } else
                    System.out.println("Not found/cancelled.");
            } else
                System.out.println("Invalid.");
        }
    }

    static void companyMenu() {
        while (true) {
            System.out.println("\n-- COMPANIES --\n1 Add  2 List  0 Back");
            int c = num("Choice: ");
            if (c == 0)
                return;
            if (c == 1) {
                String id = ask("Company ID: ");
                if (company(id) != null) {
                    System.out.println("ID exists.");
                    continue;
                }
                companies.add(new Company(id, ask("Name: "), ask("Industry: ")));
                System.out.println("Added.");
            } else if (c == 2)
                for (Company x : companies)
                    System.out.println(x.id + " | " + x.name + " | " + x.industry);
            else
                System.out.println("Invalid.");
        }
    }

    static void jobMenu() {
        while (true) {
            System.out.println("\n-- JOBS --\n1 Add  2 List  0 Back");
            int c = num("Choice: ");
            if (c == 0)
                return;
            if (c == 1) {
                String id = ask("Job ID: "), co = ask("Company ID: ");
                if (job(id) != null || company(co) == null) {
                    System.out.println("Duplicate job or unknown company.");
                    continue;
                }
                String title = ask("Title: ");
                double cg = dec("Minimum CGPA: ");
                String branches = ask("Branches comma-separated or ALL: ");
                int bl = num("Maximum backlogs: ");
                double pkg = dec("Package LPA: ");
                String skills = ask("Required skills comma-separated (blank = none): ");
                jobs.add(new Job(id, co, title, cg, branches, bl, pkg, skills));
                System.out.println("Job added.");
            } else if (c == 2)
                listJobs();
            else
                System.out.println("Invalid.");
        }
    }

    static void listJobs() {
        for (Job j : jobs) {
            Company c = company(j.cid);
            System.out.println(j.id + " | " + (c == null ? "?" : c.name) + " | " + j.title + " | min CGPA " + j.min
                    + " | " + j.pkg + " LPA");
        }
    }

    static void apply() {
        String sid = ask("Student ID: ");
        Student s = hash.get(sid);
        if (s == null) {
            System.out.println("Student not found.");
            return;
        }
        listJobs();
        String jid = ask("Job ID: ");
        Job j = job(jid);
        if (j == null) {
            System.out.println("Job not found.");
            return;
        }
        if (!eligible(s, j)) {
            System.out.println("Not eligible.");
            return;
        }
        for (Application a : apps)
            if (a.sid.equals(sid) && a.jid.equals(jid)) {
                System.out.println("Already applied.");
                return;
            }
        apps.add(new Application(sid, jid, System.currentTimeMillis()));
        System.out.println("Application submitted.");
    }

    static boolean eligible(Student s, Job j) {
        if (s == null || s.cgpa < j.min || s.backlogs > j.maxBack)
            return false;
        if (!j.branches.equalsIgnoreCase("ALL")
                && !Arrays.stream(j.branches.split(",")).map(String::trim).anyMatch(x -> x.equalsIgnoreCase(s.branch)))
            return false;
        for (String r : j.skills.split(","))
            if (!r.isBlank() && !Arrays.stream(s.skills.toLowerCase().split(",")).map(String::trim)
                    .anyMatch(x -> x.equals(r.trim().toLowerCase())))
                return false;
        return true;
    }

    static void eligibility() {
        Student s = hash.get(ask("Student ID: "));
        if (s == null) {
            System.out.println("No student.");
            return;
        }
        listJobs();
        Job j = job(ask("Job ID: "));
        System.out.println(j != null && eligible(s, j) ? "ELIGIBLE" : "NOT ELIGIBLE");
    }

    static List<Student> applicants(Job j) {
        ArrayList<Student> x = new ArrayList<>();
        for (Application a : apps)
            if (a.jid.equals(j.id)) {
                Student s = hash.get(a.sid);
                if (s != null && eligible(s, j))
                    x.add(s);
            }
        return x;
    }

    static void merit() {
        listJobs();
        Job j = job(ask("Job ID: "));
        if (j == null)
            return;
        List<Student> x = applicants(j);
        MergeSort.sort(x);
        System.out.println("MERIT LIST");
        for (int i = 0; i < x.size(); i++)
            System.out.println((i + 1) + ". " + x.get(i));
    }

    static void shortlist() {
        listJobs();
        Job j = job(ask("Job ID: "));
        if (j == null)
            return;
        MaxHeap h = new MaxHeap();
        for (Student s : applicants(j))
            h.add(s);
        int n = num("How many to shortlist? ");
        for (int i = 1; i <= n && !h.empty(); i++)
            System.out.println(i + ". " + h.pop());
    }

    static void dsa() {
        while (true) {
            System.out.println(
                    "\n-- DSA LAB --\n1 BST CGPA range  2 Hash lookup  3 Max Heap demo  4 Merge Sort demo  0 Back");
            int c = num("Choice: ");
            if (c == 0)
                return;
            if (c == 1) {
                double a = dec("Min CGPA: "), b = dec("Max CGPA: ");
                for (Student s : bst.range(a, b))
                    show(s);
                System.out.println("BST range query: average O(log n + k), worst O(n).");
            } else if (c == 2) {
                Student s = hash.get(ask("Student ID: "));
                System.out.println(s == null ? "Not found" : s);
                System.out.println("Custom chained hash table: average O(1), worst O(n).");
            } else if (c == 3) {
                MaxHeap h = new MaxHeap();
                for (Student s : students)
                    h.add(s);
                while (!h.empty())
                    System.out.println(h.pop());
                System.out.println("Heap insert/extract O(log n), peek O(1).");
            } else if (c == 4) {
                ArrayList<Student> x = new ArrayList<>(students);
                MergeSort.sort(x);
                for (Student s : x)
                    System.out.println(s);
                System.out.println("Merge Sort O(n log n), auxiliary space O(n).");
            }
        }
    }

    static void report() {
        System.out.println("Students: " + students.size() + " | Companies: " + companies.size() + " | Jobs: "
                + jobs.size() + " | Applications: " + apps.size());
    }

    static Company company(String id) {
        for (Company c : companies)
            if (c.id.equalsIgnoreCase(id))
                return c;
        return null;
    }

    static Job job(String id) {
        for (Job j : jobs)
            if (j.id.equalsIgnoreCase(id))
                return j;
        return null;
    }

    static void show(Student s) {
        System.out.println(s);
    }

    static void reindex() {
        hash = new StudentHashTable();
        bst = new StudentBST();
        for (Student s : students) {
            hash.put(s);
            bst.add(s);
        }
    }

    static void save() {
        try {
            File f = new File(FILE);
            f.getParentFile().mkdirs();
            try (ObjectOutputStream o = new ObjectOutputStream(new FileOutputStream(f))) {
                o.writeObject(new Data(students, companies, jobs, apps));
            }
            System.out.println("Saved: " + FILE);
        } catch (Exception e) {
            System.out.println("Save error: " + e.getMessage());
        }
    }

    static void load() {
        File f = new File(FILE);
        if (f.exists())
            try (ObjectInputStream o = new ObjectInputStream(new FileInputStream(f))) {
                Data d = (Data) o.readObject();
                students = d.s;
                companies = d.c;
                jobs = d.j;
                apps = d.a;
                return;
            } catch (Exception e) {
                System.out.println("Could not load saved data; loading sample records.");
            }
        seed();
    }

    static void seed() {
        students.add(new Student("S101", "Keshav Mazumdar", "CSE", 8.8, 0, "Java,DSA,SQL"));
        students.add(new Student("S102", "Aarav Sharma", "CSE", 9.4, 0, "Java,DSA,Spring"));
        students.add(new Student("S103", "Priya Verma", "ECE", 8.2, 1, "C,Java,SQL"));
        students.add(new Student("S104", "Rahul Singh", "CSE", 7.6, 0, "Java,HTML"));
        companies.add(new Company("C01", "TechNova", "IT"));
        companies.add(new Company("C02", "DataSphere", "Analytics"));
        jobs.add(new Job("J01", "C01", "Java Developer", 8, "CSE,IT", 0, 6.5, "Java,DSA"));
        jobs.add(new Job("J02", "C02", "Analyst", 7.5, "ALL", 1, 5, "SQL"));
        apps.add(new Application("S101", "J01", System.currentTimeMillis()));
        apps.add(new Application("S102", "J01", System.currentTimeMillis()));
    }

    static class Student implements Serializable {
        String id, name, branch, skills;
        double cgpa;
        int backlogs;

        Student(String i, String n, String b, double g, int bl, String sk) {
            id = i;
            name = n;
            branch = b;
            cgpa = g;
            backlogs = bl;
            skills = sk;
        }

        int skillScore() {
            return skills.isBlank() ? 0 : skills.split(",").length;
        }

        public String toString() {
            return String.format("%-6s %-20s %-8s CGPA %.2f backlogs %d skills %s", id, name, branch, cgpa, backlogs,
                    skills);
        }
    }

    static class Company implements Serializable {
        String id, name, industry;

        Company(String i, String n, String x) {
            id = i;
            name = n;
            industry = x;
        }
    }

    static class Job implements Serializable {
        String id, cid, title, branches, skills;
        double min, pkg;
        int maxBack;

        Job(String i, String c, String t, double m, String b, int mb, double p, String s) {
            id = i;
            cid = c;
            title = t;
            min = m;
            branches = b;
            maxBack = mb;
            pkg = p;
            skills = s;
        }
    }

    static class Application implements Serializable {
        String sid, jid;
        long time;

        Application(String s, String j, long t) {
            sid = s;
            jid = j;
            time = t;
        }
    }

    static class Data implements Serializable {
        ArrayList<Student> s;
        ArrayList<Company> c;
        ArrayList<Job> j;
        ArrayList<Application> a;

        Data(ArrayList<Student> x, ArrayList<Company> y, ArrayList<Job> z, ArrayList<Application> w) {
            s = x;
            c = y;
            j = z;
            a = w;
        }
    }

    // Custom BST keyed by CGPA; equal keys share a bucket.
    static class Node {
        double key;
        ArrayList<Student> bucket = new ArrayList<>();
        Node l, r;

        Node(Student s) {
            key = s.cgpa;
            bucket.add(s);
        }
    }

    static class StudentBST {
        Node root;

        void add(Student s) {
            root = add(root, s);
        }

        Node add(Node n, Student s) {
            if (n == null)
                return new Node(s);
            if (s.cgpa < n.key)
                n.l = add(n.l, s);
            else if (s.cgpa > n.key)
                n.r = add(n.r, s);
            else
                n.bucket.add(s);
            return n;
        }

        List<Student> range(double lo, double hi) {
            ArrayList<Student> x = new ArrayList<>();
            walk(root, lo, hi, x);
            return x;
        }

        void walk(Node n, double lo, double hi, List<Student> x) {
            if (n == null)
                return;
            if (lo < n.key)
                walk(n.l, lo, hi, x);
            if (n.key >= lo && n.key <= hi)
                x.addAll(n.bucket);
            if (hi > n.key)
                walk(n.r, lo, hi, x);
        }
    }

    // Custom hash table with separate chaining and resize.
    static class HNode {
        String key;
        Student val;
        HNode next;

        HNode(Student s) {
            key = s.id;
            val = s;
        }
    }

    static class StudentHashTable {
        HNode[] b = new HNode[16];
        int size;

        int idx(String k) {
            return (k.hashCode() & 0x7fffffff) % b.length;
        }

        void put(Student s) {
            int i = idx(s.id);
            for (HNode n = b[i]; n != null; n = n.next)
                if (n.key.equals(s.id)) {
                    n.val = s;
                    return;
                }
            HNode n = new HNode(s);
            n.next = b[i];
            b[i] = n;
            if (++size > b.length * .75)
                resize();
        }

        Student get(String k) {
            if (k == null)
                return null;
            for (HNode n = b[idx(k)]; n != null; n = n.next)
                if (n.key.equals(k))
                    return n.val;
            return null;
        }

        void resize() {
            HNode[] old = b;
            b = new HNode[old.length * 2];
            size = 0;
            for (HNode h : old)
                for (HNode n = h; n != null; n = n.next)
                    put(n.val);
        }
    }

    // Max heap: CGPA, skill count, then earlier application timestamp.
    static class MaxHeap {
        ArrayList<Student> a = new ArrayList<>();

        boolean empty() {
            return a.isEmpty();
        }

        boolean higher(Student x, Student y) {
            if (x.cgpa != y.cgpa)
                return x.cgpa > y.cgpa;
            if (x.skillScore() != y.skillScore())
                return x.skillScore() > y.skillScore();
            return x.id.compareTo(y.id) < 0;
        }

        void add(Student s) {
            a.add(s);
            int i = a.size() - 1;
            while (i > 0) {
                int p = (i - 1) / 2;
                if (!higher(a.get(i), a.get(p)))
                    break;
                swap(i, p);
                i = p;
            }
        }

        Student pop() {
            if (a.isEmpty())
                return null;
            Student m = a.get(0), last = a.remove(a.size() - 1);
            if (!a.isEmpty()) {
                a.set(0, last);
                down(0);
            }
            return m;
        }

        void down(int i) {
            int best = i, l = 2 * i + 1, r = l + 1;
            if (l < a.size() && higher(a.get(l), a.get(best)))
                best = l;
            if (r < a.size() && higher(a.get(r), a.get(best)))
                best = r;
            if (best != i) {
                swap(i, best);
                down(best);
            }
        }

        void swap(int i, int j) {
            Student t = a.get(i);
            a.set(i, a.get(j));
            a.set(j, t);
        }
    }

    // Stable custom Merge Sort by descending CGPA.
    static class MergeSort {
        static void sort(List<Student> x) {
            if (x.size() < 2)
                return;
            Student[] a = x.toArray(new Student[0]), t = new Student[a.length];
            ms(a, t, 0, a.length - 1);
            for (int i = 0; i < a.length; i++)
                x.set(i, a[i]);
        }

        static void ms(Student[] a, Student[] t, int l, int r) {
            if (l >= r)
                return;
            int m = (l + r) / 2;
            ms(a, t, l, m);
            ms(a, t, m + 1, r);
            int i = l, j = m + 1, k = l;
            while (i <= m && j <= r)
                t[k++] = a[i].cgpa >= a[j].cgpa ? a[i++] : a[j++];
            while (i <= m)
                t[k++] = a[i++];
            while (j <= r)
                t[k++] = a[j++];
            for (i = l; i <= r; i++)
                a[i] = t[i];
        }
    }
}
