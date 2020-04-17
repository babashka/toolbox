#!/usr/bin/env bb

(ns generate
  (:require
   [babashka.classpath :refer [add-classpath]]
   [clj-yaml.core :as yaml]
   [clojure.string :as str]
   [clojure.java.shell :refer [sh]]))

(add-classpath (:out (sh "clojure" "-Spath")))

;;;; End deps

(require '[comb.template :as template])

(def projects (->> (yaml/parse-string (slurp "projects.yml"))
                   vals
                   (sort-by #(str/lower-case (:name %)))))

(def categories
  (let [by-category (for [project projects
                          category (:categories project)]
                      [(str/trim category) project])]
    (->> (reduce (fn [acc [cat proj]]
                   (update acc cat (fnil conj []) proj))
                 {}
                 by-category)
         (sort-by #(str/lower-case (key %))))))

(def template-result (template/eval (slurp "index.html.comb") {:categories categories}))

(spit "index.html" template-result)

(println (template/eval "Written <%= (count projects) %> projects in <%= (count categories) %> categories to index."))
