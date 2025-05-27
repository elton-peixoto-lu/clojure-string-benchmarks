(ns clojure-benchmarks.core
  (:require [criterium.core :as c]
            [incanter.core :as i]
            [incanter.charts :as charts]))

(def parts (repeat 100000 "abc"))

(defn using-str-concat [parts]
  (reduce str "" parts))

(defn using-string-builder [parts]
  (let [sb (StringBuilder.)]
    (doseq [part parts]
      (.append sb part))
    (.toString sb)))

(defn log10 [x]
  (Math/log10 (max x 1e-9))) ; evita log(0)

(defn -main [& args]
  (println "Benchmark de concatenação de strings em Clojure\n")
  (println "reduce str:")
  (let [res1 (c/quick-benchmark (using-str-concat parts) {})]
    (c/report-result res1)
    (println "Valor bruto :mean reduce str:" (:mean res1))
    (println "\nStringBuilder:")
    (let [res2 (c/quick-benchmark (using-string-builder parts) {})]
      (c/report-result res2)
      (println "Valor bruto :mean StringBuilder:" (:mean res2))
      (let [mean1 (when (and (map? res1) (vector? (:mean res1))) (first (:mean res1)))
            mean2 (when (and (map? res2) (vector? (:mean res2))) (first (:mean res2)))]
        (when (and (number? mean1) (number? mean2))
          (let [labels ["reduce str" "StringBuilder"]
                vals   [(double mean1) (double mean2)] ; agora em ns
                logvals (mapv log10 vals)
                chart  (charts/bar-chart labels logvals
                                        :title "Concatenação de Strings (log₁₀ ns)"
                                        :y-label "log₁₀ Tempo médio (ns)")]
            (i/view chart)
            (println "\nValores médios (ns):")
            (println (format "reduce str    : %.2f ns" (first vals)))
            (println (format "StringBuilder : %.2f ns" (second vals))))))))) 
