#!/usr/bin/env bb

(ns generate
  (:require
   [babashka.classpath :refer [add-classpath]]
   [babashka.curl :as curl]
   [clj-yaml.core :as yaml]
   [clojure.java.io :as io]
   [clojure.string :as str]))

;;;; Deps

(def lib-dir (io/file "lib"))

;; This installs the comb templating library in ./lib
(when-not (.exists lib-dir)
  (println "Installing dependencies")
  (.mkdirs lib-dir)
  (doseq [{:keys [:file :url]}
          [{:file (io/file (io/file lib-dir "comb" "template.clj"))
            :url "https://raw.githubusercontent.com/weavejester/comb/master/src/comb/template.clj"}]]
    (io/make-parents file)
    (spit file (:body (curl/get url)))))

(add-classpath "lib")

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
