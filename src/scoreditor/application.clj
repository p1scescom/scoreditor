(ns scoreditor.application
  (:import  [javafx.animation AnimationTimer
                              KeyFrame
                              Timeline]
            [javafx.application Application
                                Platform]
            [javafx.beans InvalidationListener]
            [javafx.beans.property SimpleIntegerProperty]
            [javafx.beans.value ChangeListener]
            [javafx.collections FXCollections
                                ObservableList]
            [javafx.event ActionEvent
                          EventHandler]
            [javafx.scene Scene
                          Group]
            [javafx.scene.input KeyEvent]
            [javafx.scene.media Media
                                MediaView
                                MediaPlayer
                                MediaPlayer$Status
                                AudioClip]
            [javafx.scene.control Alert
                                  Alert$AlertType
                                  Button
                                 ; ButtonBar
                                 ; ButtonBar$ButtonData
                                  ButtonType
                                  Label
                                  ListView
                                  Menu
                                  MenuItem
                                  MenuBar
                                  TableView
                                  TableView$TableViewFocusModel
                                  TableView$TableViewSelectionModel
                                  TableColumn
                                  TextInputDialog
                                  SelectionMode
                                  Slider
                                  ]
            [javafx.scene.control.cell PropertyValueFactory]
            [javafx.scene.canvas Canvas
                                 GraphicsContext]
            [javafx.scene.layout AnchorPane
                                 StackPane
                                 VBox
                                 HBox
                                 BorderPane
                                 Pane]
            [javafx.scene.paint Color]
            [javafx.stage Stage
                          DirectoryChooser
                          FileChooser
                          FileChooser$ExtensionFilter]
            [javafx.util Duration
                         Callback]
            [java.io File
                     FilenameFilter]
            [java.util ArrayList]
            ColorCulmCell
            FPSData
            ResizeCanvas
            )
  (:require [clojure.java.io :as io]
            [clojure.string :as clostr])
  (:gen-class
    :extends javafx.application.Application
    :name scoreditor.application))

(def ^Stage window (promise))

(defn start-fx []
  (when (not (realized? window))
    (doto (Thread.
            #(Application/launch scoreditor.application (into-array String [])))
      .start))
  @window)

(def ^File editing-score (atom nil))

(def ^File projectDirectory (atom nil))

(defn file-uri [^File filename]
  (.. filename getPath toString))

(def ^Media media (atom nil))

(def ^MediaPlayer mediaPlayer (atom nil))

(def ^MediaView mediaView (atom nil))

(def milli-flame 20)

(def score-changed (atom false))

(defn schange []
      (reset! score-changed true))

(def media-duration (atom nil))

(defn media-millis [] (-> @media-duration .toMillis))

(defn media-fps [] (+ (if-not (zero? (rem (media-millis) milli-flame)) 1 0) (quot (media-millis) milli-flame)))

(def note-mode (atom 1))

(def flag-time (atom 0))

(def se-sound (doall (map #(AudioClip. %) (repeat 50 (.. (io/resource "SE.wav") toString)))))

(def se-played (atom false))

(def num-of-buttons 7)

(def score-rows (take num-of-buttons (clostr/split "abcdefghijklmnopqrstuvwxyz" #"")))

(defn play-se [b n a]
  (when (and (not @se-played) (or (= n 3) (= n 1) (and (= 2 n) (or (= b 0) (= a 0)))) )
      (.play (some
               #(when-not (.isPlaying %) %)
               se-sound))
      (reset! se-played true)))

(def before-frame (atom -1))

(defmacro get-rows [target]
  (let [gets (map #(symbol (str ".get" (clostr/upper-case %))) score-rows)]
    (cons 'list (map #(list % target) gets))))

(defmacro get-objs [obj]
  (let [gets (map #(symbol (str ".get" (clostr/upper-case %))) score-rows)]
    (cons 'list (map #(list % obj) gets))))

(defmacro save-notes []
  (let [gets (map #(symbol (str ".get" (clostr/upper-case %))) score-rows)
        return (cons 'do (map (fn [one-get alpha]
                                (println (list alpha))
                                `(spit (File. @editing-score ~alpha)
                                       (str (apply str (map #(~one-get %) `score-observ)) \4)))
                              gets score-rows))]
    (println return)
    return))

(defmacro makeFPS []
  (let [alphas (cons 'flame (map #(symbol %) score-rows))
        ls (cons 'FPSData. alphas)
        return `(fn ~(vector (vec alphas)) ~(cons println alphas) ~ls)]
    (println return)
    return))

(defn -start [this stage]
  (deliver window stage)
  (let [root        (AnchorPane.)
        controlers  (HBox.)
        score-table (TableView.)

        preview-pane      (Pane.)
        preview-canvas    (ResizeCanvas.)
        time-label        (Label. "Choice Project")
        slider-time       (Slider.)
        slider-volume     (Slider. 0.0 1.0 1.0)
        play-pause-button (Button. "Start")

        score-observ (FXCollections/observableArrayList)

        project-name-label (Label. "Choice Project Directory")
        score-name-label   (Label. "Choice Score")
        project-duration   (Label. "Choice Project Directory")
        now-note-label     (Label. "")
        menubar        (MenuBar.)

        se (future
             (while true
               (when-not (nil? @media-duration)
                 (let [now-time  (.getCurrentTime @mediaPlayer)
                       milli-now (.toMillis now-time)
                       fps       (/ milli-now milli-flame)
                       c         1.0
                       frame-num (quot (* c 1000 ) milli-flame)
                       now-t     (quot milli-now milli-flame)]

                   (when (< now-t (- (media-fps) 1))
                     (let [media-status (.getStatus @mediaPlayer)]
                       (when-not (or (= media-status MediaPlayer$Status/PAUSED ) (= media-status MediaPlayer$Status/STOPPED)  (= media-status MediaPlayer$Status/READY) (= @before-frame now-t))
                         (reset! before-frame now-t)
                         (dorun (map (fn [b n a] (play-se b n a))
                                     (if (>= 1 now-t) (list 0 0 0 0)
                                         (let [ob (.get score-observ (- now-t 2))]
                                           (dorun (map #(.getNum ob %) (range num-of-buttons)))
                                           #_(get-rows ob)
                                           #_(list (.getA ob) (.getB ob) (.getC ob) (.getD ob))))
                                     (if (>= 0 now-t) (list 0 0 0 0)
                                         (let [ob (.get score-observ (- now-t 1))]
                                           (dorun (map #(.getNum ob %) (range num-of-buttons)))
                                           #_(map #(eval (list (symbol ".get" %) ob)) (map clostr/upper-case score-rows))
                                           #_(list (.getA ob) (.getB ob) (.getC ob) (.getD ob))))
                                        ;(if (< (- (media-fps) 1) now-t) (list 0 0 0 0)
                                     (let [ob (.get score-observ now-t )]
                                       (dorun (map #(.getNum ob %) (range num-of-buttons)))
                                       #_(map #(eval (list (symbol ".get" %) ob)) (map clostr/upper-case score-rows))
                                       #_(list (.getA ob) (.getB ob) (.getC ob) (.getD ob)));)
                                     ))))
                     )))
               (Thread/sleep 1)))

        scene (Scene. root 1310 514)]

  (letfn [

    (check-save []
      (when-not @score-changed
        (let [blist (make-array ButtonType 2)]
        (aset blist 0 ButtonType/YES)
        (aset blist 1 ButtonType/NO)
        (let [al (Alert. Alert$AlertType/NONE "" blist)]
          (doto al
            (.setTitle "確認")
            (-> .getDialogPane (.setContentText "保存してスコアを変更しますか？")))
          (let [kai (.toString (.getButtonData (-> al .showAndWait (.orElse ButtonType/CANCEL))))]
            (case kai
              "YES"    (do (score-save))
              "NO"     ()
              "CANCEL" (println "ええんかい！")
              ))))))

    (shutdown [com]
      (if-not @score-changed (do (shutdown-agents) (future-cancel se) (Platform/exit))
              (let [blist (make-array ButtonType 3)]
          (aset blist 0 ButtonType/YES)
          (aset blist 1 ButtonType/NO)
          (aset blist 2 ButtonType/CANCEL)
          (let [al (Alert. Alert$AlertType/NONE "" blist)]
            (doto al
              (.setTitle "確認")
              (-> .getDialogPane (.setContentText "保存して終了しますか？")))
            (let [kai (.toString (.getButtonData (-> al .showAndWait (.orElse ButtonType/CANCEL))))]
              (case kai
                "YES"    (do (score-save)
                             (shutdown-agents)
                             (future-cancel se)
                             (System/exit 0))
                "NO"     (do (future-cancel se)
                             (shutdown-agents)
                             (System/exit 0))
                "CANCEL" (do (.consume com))
                (do (.consume com) (println kai "OTHER"))
                ))

          ))))

    (stage-set []
      (doto stage
        (.addEventFilter KeyEvent/KEY_PRESSED (proxy [EventHandler] []
                                                (handle [e]
                                                  (let [inp (.getText e)]
                                                    (println inp)
                                                    (case inp
                                                      "a" (do (note-change 0)
                                                              #_(-> score-table  .getSelectionModel (.setSelectionMode SelectionMode/MULTIPLE)))
                                                      "s" (if (.isControlDown e) (score-save)
                                                              (do (note-change 1)
                                                                  #_(-> score-table  .getSelectionModel (.setSelectionMode SelectionMode/SINGLE))))
                                                      "d" (do (note-change 2)
                                                              #_(-> score-table  .getSelectionModel (.setSelectionMode SelectionMode/MULTIPLE)))
                                                      "default")))))
        (.setOnCloseRequest (proxy [EventHandler] []
                              (handle [e]
                                (shutdown e))))
        (.setMinHeight 514)
        (.setMinWidth 1310)))

    (ready-func []
      (doto @mediaPlayer
        (.setOnReady
          (fn []
            (doto (-> @mediaPlayer .currentTimeProperty )
              (.addListener (proxy [ChangeListener] []
                              (changed [ob ol ne]
                                (updata-values))))
              (.addListener (proxy [InvalidationListener] []
                              (invalidated [e]
                                (updata-values)))))
            (reset! media-duration (.getDuration @media))
            (updata-values)
            (.setText project-name-label (file-uri @projectDirectory))
            (.setText project-duration (str (media-millis)))
              ))))

    (make-project []
      (let [config-file (File. @projectDirectory "config.txt")]
        (with-open [hoge (io/reader config-file)]
          (let [music-file (File. @projectDirectory
                                  (do (.readLine hoge) (.readLine hoge) (.readLine hoge)))]
          (if (nil? music-file) false
            (do
              (reset! media (Media. (.. music-file toURI toString)))
              (reset! mediaPlayer (MediaPlayer. @media))
              ;(.setAutoPlay @mediaPlayer true)
              (reset! mediaView (MediaView. @mediaPlayer))))

      (doto @mediaPlayer
        (.setOnReady
          (fn []
            (let [score-file (File. @projectDirectory "score")
                  scores     (map #(File. score-file %) score-rows)]
              (reset! media-duration (.getDuration @media))
              (.mkdir score-file)
              (let [all-zero (str (apply str (repeat (media-fps) \0) ) \4)]
                (dorun (map #(spit % all-zero) scores)))
                (score-set score-file)
                (input-score-set))
            (doto (-> @mediaPlayer .currentTimeProperty)
              (.addListener (proxy [ChangeListener] []
                              (changed [ob ol ne]
                                (updata-values))))
              (.addListener (proxy [InvalidationListener] []
                              (invalidated [e]
                                (updata-values)))))
            (updata-values)
            (.setText project-name-label (file-uri @projectDirectory))
            (.setText project-duration (str (media-millis)))
              ))) true))))


    (media-set []
      (let [config-file (File. @projectDirectory "config.txt")
            music-file  (File. @projectDirectory (with-open [hoge (io/reader config-file)]
                                                   (do (.readLine hoge) (.readLine hoge) (.readLine hoge))))]
        (if (nil? music-file) false
          (do
            (reset! media (Media. (.. music-file toURI toString)))
            (reset! mediaPlayer (MediaPlayer. @media))
            ;(.setAutoPlay @mediaPlayer true)
            (reset! mediaView (MediaView. @mediaPlayer))
            (ready-func)
            true))))

    (note-change [x] (reset! note-mode x))

    (score-save []
      (when score-changed
        (when-not (nil? editing-score)
          #_(let [score-a (str (apply str (map #(.getA %) score-observ)) \4)
                  score-b (str (apply str (map #(.getB %) score-observ)) \4)
                  score-c (str (apply str (map #(.getC %) score-observ)) \4)
                  score-d (str (apply str (map #(.getD %) score-observ)) \4)]
            (spit (File. @editing-score "a") score-a)
            (spit (File. @editing-score "b") score-b)
            (spit (File. @editing-score "c") score-c)
            (spit (File. @editing-score "d") score-d))
          #_(apply map #(spit (File. @editing-score %) %2) [score-rows (let [an (get-rows score-rows score-observ)]
                                                                       (map #((first % ) )))])
          (dorun (map (fn [alpha num] (spit (File. @editing-score alpha)
                                            (str (apply str (map #(.get % num) score-observ)) \4)))
                      score-rows (range num-of-buttons)))
          (save-notes)
          (reset! score-changed false))))

    (score-no-set []
      (reset! editing-score nil)
      (.setText score-name-label "Choice Score"))

    (score-set [^File dir]
      (reset! editing-score dir)
      (.setText score-name-label (.getName dir)))

    (score-level-change []
      ())

    (menubar-set []
      (let [directoryChooser           (DirectoryChooser.)
            fileChooser                (FileChooser.)
            menu-directory             (Menu. "File")
            menu-item-new-directory    (MenuItem. "New Project")
            menu-item-choice-directory (MenuItem. "Choice Project")
            menu-item-exit             (MenuItem. "Exit")
            menu-score                 (Menu. "Score")
            menu-item-new-score        (MenuItem. "New Score")
                                        ;menu-item-easy-score (MenuItem. "Easy Score")
                                        ;menu-item-normal-score (MenuItem. "Normal Score")
                                        ;menu-item-hard-score (MenuItem. "Hard Score")
            menu-item-change-score-level      (MenuItem. "Change Score Level")
            menu-item-save-score       (MenuItem. "Save Score")]

        (doto directoryChooser
          (.setTitle "Open Project Directory"))

        (doto fileChooser
          (.setTitle "Choise Using Music File")
          (-> .getExtensionFilters (.add (FileChooser$ExtensionFilter. "Audio Files" ["*.wav"]))))

        (doto menu-item-new-directory
          (.setOnAction (proxy [EventHandler] []
                          (handle [e]
                            (let [f (.showDialog directoryChooser stage)]
                              (when (not (nil? f))
                                (reset! projectDirectory f)
                                (let [inf (.showOpenDialog fileChooser stage)]
                                  (if (not (nil? inf))
                                    (let [ouf         (io/file (file-uri @projectDirectory) (.getName inf))
                                          config-file (File. @projectDirectory "config.txt")
                                          ]
                                      (io/copy inf ouf)
                                      (spit config-file (str (.getName @projectDirectory) "\n"))
                                      (let [music-str (.getName inf)]
                                        (spit config-file (str (-> music-str (.substring 0 (-> music-str (.lastIndexOf ".")))) "\n") :append true)
                                        (spit config-file music-str :append true))
                                      (when (make-project)
                                        (.setDisable menu-item-new-score false)
                                        (.setDisable menu-item-change-score-level false)
                                        (.setDisable menu-item-save-score false)
                                      ))
                                    (reset! projectDirectory nil)))))))))

        (doto menu-item-choice-directory
          (.setOnAction (proxy [EventHandler] []
                          (handle [e]
                            (let [f (.showDialog directoryChooser stage)]
                              (when (not (nil? f))
                                (reset! projectDirectory f)
                                (when (media-set)
                                  (.setDisable menu-item-new-score false)
                                  (.setDisable menu-item-change-score-level false)
                                  (.setDisable menu-item-save-score false)
                                  (score-set (File. @projectDirectory "score"))
                                  (input-score-set)
                                )))))))
        (doto menu-item-exit
          (.setOnAction (proxy [EventHandler] []
                          (handle [e]
                            (shutdown)))))
        (-> menu-directory
           .getItems (.addAll [menu-item-new-directory menu-item-choice-directory]))

        (doto menu-item-new-score
          (.setDisable true)
          (.setOnAction (proxy [EventHandler] []
                          (handle [e]
                            (let [score-name-dialog (TextInputDialog. )
                                  score-name        (-> score-name-dialog .showAndWait (.orElse ""))
                                  ]
                              (println score-name)
                              (when (not= "" score-name)
                                (let [^File score-file (File. @projectDirectory score-name)]
                                  (.mkdir score-file)
                                  (let [all-zero (apply str (repeat (media-fps) \0))]
                                    (dorun (map #(spit % all-zero) (map #(File. score-file %) score-rows)))
                                    (score-set score-file)
                                    (input-score-set)
                              ))))))))

        #_(doto menu-item-easy-score
          (.setOnAction (proxy [EventHandler] []
                          (handle [e]
                            (let [^File score-file (File. @projectDirectory "easy")]
                              (do (.pause @mediaPlayer)
                                (.setText play-pause-button "Start"))
                              (check-save)
                              (score-set score-file)
                              (input-score-set)
                              (.setDisable menu-item-save-score false)
                             )))))

        #_(doto menu-item-normal-score
          (.setOnAction (proxy [EventHandler] []
                          (handle [e]
                            (let [^File score-file (File. @projectDirectory "normal")]
                              (do (.pause @mediaPlayer)
                                (.setText play-pause-button "Start"))
                              (check-save)
                              (score-set score-file)
                              (input-score-set)
                              (.setDisable menu-item-save-score false)
                             )))))

        #_(doto menu-item-hard-score
          (.setOnAction (proxy [EventHandler] []
                          (handle [e]
                            (let [^File score-file (File. @projectDirectory "hard")]
                              (do (.pause @mediaPlayer)
                                (.setText play-pause-button "Start"))
                              (check-save)
                              (score-set score-file)
                              (input-score-set)
                              (.setDisable menu-item-save-score false)
                             )))))

        (doto menu-item-change-score-level
          (.setDisable true)
          (.setOnAction (proxy [EventHandler] []
                          (handle [e]
                            (score-level-change)))))

        (doto menu-item-save-score
          (.setDisable true)
          (.setOnAction (proxy [EventHandler] []
                          (handle [e]
                            (score-save)
                            (.setDisable menu-item-save-score false)))))

        (-> menu-score
            .getItems (.addAll [menu-item-change-score-level menu-item-save-score]))

        (-> menubar
          .getMenus (.addAll [menu-directory menu-score]))))

    (input-score-set []
      (.clear score-observ)

      (when-not (nil? @editing-score)
        (let [;a-list      (map #(Character/getNumericValue %) (slurp (File. @editing-score "a")))
              ;b-list      (map #(Character/getNumericValue %) (slurp (File. @editing-score "b")))
              ;c-list      (map #(Character/getNumericValue %) (slurp (File. @editing-score "c")))
              ;d-list      (map #(Character/getNumericValue %) (slurp (File. @editing-score "d")))
              score-lists  (let [tmp (doall (map (fn [alpha]
                                                   (map #(Character/getNumericValue %)
                                                        (slurp (File. @editing-score alpha))))
                                                 score-rows))]
                             (cons (range 1 (count (first tmp))) tmp))
              #_(vec (cons (iterate inc 1)
                                     (loop [score-rem (reverse score-rows)
                                            lists     ()]
                                       (if (empty? score-rem) lists
                                           (let [adding-list (map #(Character/getNumericValue %) (slurp (File. @editing-score (first score-rem))))]
                                             (recur (rest score-rem) (cons adding-list lists)))))))]
          (dorun
            (map #(.add score-observ %) (map (makeFPS) (apply map list score-lists))))
          #_(dorun
           (map #(.add score-observ %) (map #(eval (cons 'FPSData. %)) (apply map list score-lists))))
          #_(dorun
            (map #(.add score-observ %) (map (fn [f a b c d] (FPSData. f a b c d)) f-num a-list b-list c-list d-list))))
            (reset! before-frame -1)
            (.remove score-observ (- (.size score-observ) 1))
            (.setItems score-table score-observ)))

    (input-score-start []
      (let [timeCul (TableColumn. "TIME")
           ; aCul    (TableColumn. "A")
           ; bCul    (TableColumn. "B")
           ; cCul    (TableColumn. "C")
           ; dCul    (TableColumn. "D")
            culs    (doall (map #(TableColumn. %) (map clostr/upper-case score-rows)))]

        (doto score-table
          (-> .getSelectionModel (.setCellSelectionEnabled true))
          (.setMaxWidth  420.0)
          (.setMinWidth  420.0))

        (input-score-set)

        (doto score-table
          (-> .getFocusModel
              .focusedCellProperty
              (.addListener
                (proxy [ChangeListener] []
                  (changed [ob ol ne]
                    (let [poti (-> score-table .getFocusModel .getFocusedCell)
                          row  (.getRow poti)
                          cul  (.getTableColumn poti)]
                      (when (and (not= cul timeCul) (<= 0 row))
                        (when 
                          (let [tar (.getCellObservableValue cul row)]
                            (schange)
                            (.set tar (if (or (= (.get tar) @note-mode) (and (= @note-mode 1) (= 3 (.get tar)))) 0 @note-mode))
                            (when-not (nil? @editing-score)
                              (let [notes       (doall (map
                                                        #(let [tmp (.getCellObservableValue % row)]
                                                            (println "row" row "tmp" tmp) tmp)
                                                        culs))
                                                #_(list (.getCellObservableValue aCul row) (.getCellObservableValue bCul row)(.getCellObservableValue cCul row)(.getCellObservableValue dCul row))
                                    one-notes   (filter #(= 1 (.get %)) notes)
                                    three-notes (filter #(= 3 (.get %)) notes)
                                    now         (concat one-notes three-notes)
                                    one-three   (atom 0)]
                                (dorun (map #(do (swap! one-three inc) (println %)) now))
                                (if (>= @one-three 2)
                                  (dorun (map #(.set % 3) one-notes))
                                  (when (>= @one-three 1)
                                    (dorun (map #(.set % 1) three-notes)))))
                            #_(.focus (.getFocusModel score-table) -1)
                            #_(.clearSelection (.getSelectionModel score-table))))))))))))

        (doto timeCul
          (.setCellValueFactory (PropertyValueFactory. "flame")))

        (dorun
          (map
           #(doto %1
              (println "----" %2)
              (.setSortable false)
              (.setCellFactory (proxy [Callback] []
                                 (call [param]
                                   (ColorCulmCell.))))
              (.setCellValueFactory (PropertyValueFactory. %2)))
           culs score-rows))

        #_(doto aCul
          (.setSortable false)
          (.setCellFactory (proxy [Callback] []
                             (call [param]
                               (ColorCulmCell.))))
          (.setCellValueFactory (PropertyValueFactory. "a")))

        (doto score-table
          (-> .getColumns (.addAll (vec (cons timeCul culs)))))

        #_(doto score-table
          (-> .getColumns (.addAll [timeCul aCul bCul cCul dCul])))))

    (preview-set []
      (doto preview-pane
        (-> .getChildren (.add preview-canvas)))

      (doto preview-canvas
        (-> .heightProperty (.bind (.heightProperty preview-pane)))
        (-> .widthProperty (.bind (.widthProperty preview-pane))))

      (let [anime (proxy [AnimationTimer] []
                    (handle [now]
                      (let [gc       (.getGraphicsContext2D preview-canvas)
                            c-height (.getHeight preview-canvas)
                            c-width  (.getWidth preview-canvas)]
                        (doto gc
                          (.setLineWidth 2.0)
                          (.setFill Color/AQUAMARINE)
                          (.fillRect 0 0 c-width c-height)
                          (.setFill Color/RED)
                          (.fillRect 0 (* 18 (quot c-height 20))  c-width (* 1 (quot c-height 20))))
                        (when-not (nil? @media-duration)
                          (let [now-time  (.getCurrentTime @mediaPlayer)
                                milli-now (.toMillis now-time)
                                fps       (/ milli-now milli-flame)
                                c         1.0
                                frame-num (quot (* c 1000) milli-flame)
                                now-t     (quot milli-now milli-flame)]
                            (when (< now-t (- (media-fps) 1))
                              (letfn [(print-note [color place zikan]
                                        (when (and (not= 0 color) (not= zikan milli-now))
                                          (let [mm (/ (+ (/ (* c 1000) milli-flame) (- fps zikan)) (/ (* c 1000) milli-flame))]
                                            (doto gc
                                              (.setFill (get ColorCulmCell/colors color)))
                                            (doto gc
                                              (.fillRect (+ (quot c-width 16) (* place (quot c-width num-of-buttons))) #_(/ c-height 3) (* 0.90 c-height mm) (quot c-width (* 2 num-of-buttons)) (quot (* 0.925 c-height) frame-num))))))]
                                (dorun
                                 (map
                                  (fn [n]
                                    (when (> (.size score-observ) n)
                                          (let [tob (.get score-observ n)]
                                            (dorun (map #(print-note (.getNum tob %) % n) (range num-of-buttons)))
                                            #_(apply map #(print-note % %2 n) [(get-objs tob) (range num-of-buttons)])
                                            ;(print-note (.getA tob) 0 n)
                                            ;(print-note (.getB tob) 1 n)
                                            ;(print-note (.getC tob) 2 n)
                                            ;(print-note (.getD tob) 3 n)
                                            (reset! se-played false))))
                                    (take frame-num (iterate inc now-t)))))))))))]
        (.start anime)))

    (updata-values []
      (when-not (or (nil? @media-duration) (nil? time-label) (nil? slider-time))
        (Platform/runLater (fn []
                             (let [current-time (.getCurrentTime @mediaPlayer)]
                               (.setText time-label (str (+ (if-not (zero? (rem (.toMillis current-time) milli-flame)) 1 0) (quot (.toMillis current-time) milli-flame))))
                               (.setDisable slider-time (.isUnknown @media-duration))
                               (when (and (not (.isDisabled slider-time)) (. @media-duration greaterThan (Duration/ZERO)) (not (.isValueChanging slider-time)))
                                 (.setValue slider-time (* (-> current-time (.divide @media-duration) .toMillis) 100.0))))))))

    (controler-set []
      (let [reload-button     (Button. "最初から")
            flag-button       (Button. "旗")
            flag-start-button (Button. "旗start")]
        (doto play-pause-button
          (.setOnAction (proxy [EventHandler] []
                          (handle [e]
                            (updata-values)
                            (let [media-status (.getStatus @mediaPlayer)]
                              (if (or (= media-status MediaPlayer$Status/PAUSED ) (= media-status MediaPlayer$Status/STOPPED)  (= media-status MediaPlayer$Status/READY ))
                                (do (.play @mediaPlayer)
                                  (.setText play-pause-button "Stop"))
                                (do (.pause @mediaPlayer)
                                  (.setText play-pause-button "Start"))))))))
        (doto reload-button
          (.setOnAction (proxy [EventHandler] []
                          (handle [e]
                            (doto @mediaPlayer (.seek (.getStartTime @mediaPlayer)))))))

        (doto flag-button
          (.setOnAction (proxy [EventHandler] []
                          (handle [e]
                            (reset! flag-time (.getCurrentTime @mediaPlayer))))))

        (doto flag-start-button
          (.setOnAction (proxy [EventHandler] []
                          (handle [e]
                            (doto @mediaPlayer (.seek @flag-time))))))

        (doto controlers
          (-> .getChildren (.addAll [play-pause-button reload-button flag-button flag-start-button])))

        (doto slider-time
          (-> .valueProperty (.addListener (proxy [InvalidationListener] []
                                             (invalidated [e]
                                               (when (.isValueChanging slider-time)
                                                 (do (when-not (nil? @media-duration)
                                                   (.seek @mediaPlayer (.multiply @media-duration (/ (.getValue slider-time) 100.0))))
                                                   (updata-values))))))))
        (doto slider-volume
          (-> .valueProperty (.addListener (proxy [InvalidationListener] []
                                             (invalidated [e]
                                               (when (.isValueChanging slider-volume)
                                                  (do (when-not (nil? @media-duration))
                                                    (.setVolume @mediaPlayer (.getValue slider-volume)))))))))
        ))

    (place-set []

      (doto root
        (-> .getChildren (.addAll [menubar project-name-label score-name-label project-duration score-table controlers now-note-label preview-pane slider-time time-label])))

      (AnchorPane/setTopAnchor menubar 0.0)
      (AnchorPane/setRightAnchor menubar 0.0)
      (AnchorPane/setLeftAnchor menubar 0.0)

      (AnchorPane/setTopAnchor  project-name-label 32.0)
      (AnchorPane/setLeftAnchor project-name-label 10.0)

      (AnchorPane/setTopAnchor  project-name-label 32.0)
      (AnchorPane/setLeftAnchor project-name-label 200.0)

      (AnchorPane/setTopAnchor score-name-label 46.0)
      (AnchorPane/setLeftAnchor score-name-label 200.0)

      (AnchorPane/setTopAnchor  project-duration 60.0)
      (AnchorPane/setLeftAnchor project-duration 200.0)

      (AnchorPane/setTopAnchor score-table 80.0)
      (AnchorPane/setLeftAnchor score-table 200.0)
      (AnchorPane/setBottomAnchor score-table 100.0)

      (AnchorPane/setTopAnchor preview-pane 80.0)
      (AnchorPane/setLeftAnchor preview-pane 700.0)
      (AnchorPane/setRightAnchor preview-pane 100.0)
      (AnchorPane/setBottomAnchor preview-pane 100.0)

      (AnchorPane/setBottomAnchor slider-time 50.0)
      (AnchorPane/setLeftAnchor slider-time 700.0)
      (AnchorPane/setRightAnchor slider-time 100.0)

      (AnchorPane/setBottomAnchor time-label 25.0)
      (AnchorPane/setRightAnchor time-label 100.0)

      (AnchorPane/setBottomAnchor controlers 20.0)
      (AnchorPane/setLeftAnchor controlers 200.0)
    )
    ]

    (stage-set)

    (menubar-set)

    (input-score-start)

    (preview-set)

    (controler-set)

    (place-set)

    (doto stage
      (.setScene scene) .show))))
