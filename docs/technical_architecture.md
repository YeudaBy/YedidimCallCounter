# אפיון טכני וארכיטקטורה: אפליקציית "ידידים - מונה שיחות"

## 1. טכנולוגיות מרכזיות
- **שפה ומסגרת עבודה:** Kotlin Multiplatform (KMP)
- **מודול Shared (לוגיקה משותפת):** יכיל מודלים, אימותים (Validations), לוגיקה עסקית, התממשקות לרשת, ולוגיקה של זיהוי מצב התחברות.
- **מודול App (אנדרואיד):** UI באמצעות Jetpack Compose, גישה רגישה ליומן שיחות (`READ_CALL_LOG`) וחיוג (`CALL_PHONE`), שירותי פוש (FCM), Widget (AppWidgetProvider/Glance).
- **מודול Backend (שרת):** Ktor (מבוסס קוטלין) / Spring Boot או פריימוורק שרת תואם KMP.
- **מסד נתונים (Backend):** PostgreSQL.
- **שירותי ענן ותשתיות:** Firebase Cloud Messaging (FCM) לשליחת פושים ו"חיזוקים".

## 2. מבנה פרויקט ו-Modules
```text
CallsCounter/
├── app/                  # קוד אנדרואיד (Jetpack Compose, Android Manifest, Permissions)
├── shared/               # קוד KMP - מודלים, Business Logic, Network Clients
└── backend/              # קוד השרת (PostgreSQL, Ktor REST API, Firebase Admin SDK)
```

## 3. מודל נתונים מרכזי (Data Models)
### User
- `id` (UUID)
- `name` (String)
- `region` (Enum: JERUSALEM, NORTH, SOUTH, CENTER)
- `dispatcherNumber` (String)
- `role` (Enum: REGULAR, HIGH_SCHOOL_STUDENT, SHIFT_MANAGER, MANAGER)
- `fcmToken` (String - עבור שליחת התראות)

### CallRecord
- `callId` (String - Local ID)
- `timestamp` (Long/Instant)
- `durationSeconds` (Int)
- `type` (Enum: INCOMING, OUTGOING)

### LeaderboardEntry
- `userId` (UUID)
- `name` (String)
- `callsCount` (Int)
- `rank` (Int)

## 4. מנגנונים ויכולות טכניות באנדרואיד (App Module)

### 4.1. הרשאות (Permissions)
- `READ_CALL_LOG`: סריקת יומן השיחות עבור המספרים הרלוונטיים (חובה לאישור על ידי גוגל פליי, יש להסביר את מטרת האפליקציה היטב).
- `CALL_PHONE`: ביצוע חיוג מהיר למוקד להתחברות/התנתקות עם סיסמה.
- `POST_NOTIFICATIONS`: התראות דחיפה והתראות מקומיות.

### 4.2. מעקב יומן שיחות
- שימוש ב- `ContentResolver` בשאילתה מול `CallLog.Calls.CONTENT_URI`.
- סינון לפי `NUMBER IN ('1230', '0533131310')`.
- שאילתה לפי תאריך (גדול מתחילת שבוע מוקד האחרון).

### 4.3. מעקב סטטוס התחברות (Heuristics)
- **זיהוי התחברות:** אם בוצעה שיחה יוצאת למספר 1230, יישמר טיימסטאמפ של "התחברות משוערת". במידה ומתקבלות שיחות נכנסות לאחר מכן, נצא מנקודת הנחה שהמשתמש מחובר.
- **זיהוי התנתקות:**
  1. שיחה יוצאת נוספת למספר 1230.
  2. טיימר של 20 דקות ללא אף שיחה נכנסת (ניתן לנהל באמצעות `WorkManager` או `AlarmManager` שמתאפסים בכל שיחה נכנסת).

## 5. ארכיטקטורת שרת (Backend)

### 5.1. Endpoints
- `POST /api/users` - יצירת או עדכון משתמש (שם, מחוז, פוש טוקן).
- `POST /api/calls/sync` - סנכרון כמות שיחות שבועי (עדכון count ב-DB).
- `GET /api/leaderboard?region={region}&type={weekly|night}` - החזרת Top 10.
- `POST /api/kudos` - שליחת הודעת פירגון. השרת ישתמש ב-Firebase Admin SDK כדי לשלוח הודעת דחיפה לפוש טוקן של המשתמש הרלוונטי.
- `POST /api/reports` - דיווח תקלות מוקד.

### 5.2. התממשקות Firebase
- שמירת מפתחות שרת של Firebase (Service Account) ב-Backend.
- ה-App שולח את ה-FCM Token ל-Backend ב-Login / App Start.
- כשמשתמש A שולח פירגון למשתמש B, ה-Backend מקבל בקשה, בודק ב-DB את הטוקן של B, ומוציא Push Notification דרך Firebase.

## 6. סנכרון נתונים מקומי ומרוחק
- האפליקציה תסנכרן את נתוני ה-Call Log באופן לוקאלי על המכשיר (Room DB או DataStore לקאש).
- אחת לזמן מה (למשל כל 15 דקות כשהאפליקציה פתוחה, או באמצעות WorkManager), האפליקציה תשלח לשרת את **סיכום כמות השיחות השבועית** של המשתמש, כדי לא להעמיס וכדי לשמור על פרטיות. אין צורך לשלוח את כל פירוט השיחות לשרת, אלא רק מספרי אגרגציה.

## 7. תובנות (Insights Logic)
- הלוגיקה שמחשבת את השעה החזקה ביותר, היום העמוס ביותר וכדומה - תתבצע מקומית באפליקציה על ידי ניתוח היסטוריית השיחות במכשיר (כחלק ממודול ה-Shared), כדי להבטיח ביצועים ולשמור על פרטיות.
