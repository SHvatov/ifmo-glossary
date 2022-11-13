<template>
  <b-container fluid id="glossary-container">
    <b-container style="margin-bottom: 2%; padding: 0">
      <b-row>
        <h1>Термины дипломной работы</h1>
      </b-row>
      <b-row>
        <h5>Выполнил студент группы P41081 Хватов Сергей</h5>
      </b-row>
    </b-container>
    <b-row>
      <b-col lg="8">
        <b>
          <b-form-group
            label="Сортировать по наименованию"
            label-for="sort-by-name"
            label-cols-sm="6"
            label-align-sm="right"
            label-size="sm"
            class="lg"
          >
            <b-form-select
              id="sort-by-name"
              v-model="sortOrder"
              size="sm"
              class="lg"
            >
              <option :value="'ASC'">По возрастанию</option>
              <option :value="'DESC'">По убыванию</option>
            </b-form-select>
          </b-form-group>
        </b>
      </b-col>
    </b-row>

    <b-row>
      <b-col lg="8">
        <b>
          <b-form-group
            label="Фильтровать по наименованию"
            label-for="filter-input"
            label-cols-sm="6"
            label-align-sm="right"
            label-size="sm"
            class="lg"
          >
            <b-input-group size="sm">
              <b-form-input
                id="filter-input"
                v-model="filter"
                type="text"
                placeholder="Введите подстроку для фильтрации..."
              >
              </b-form-input>

              <b-input-group-append>
                <b-button @click="clearFilter()"> Очистить </b-button>
              </b-input-group-append>
            </b-input-group>
          </b-form-group>
        </b>
      </b-col>
    </b-row>

    <b-row>
      <b-col lg="3">
        <b-button @click="loadGlossary()"> Применить фильтры </b-button>
      </b-col>

      <b-col lg="3">
        <b-button v-b-modal.glossary-modal>Добавить новый термин</b-button>

        <b-modal
          id="glossary-modal"
          size="lg"
          title="Новый термин"
          ok-only
          ok-title="Добавить"
          @ok="createGlossaryItem()"
        >
          <b-form-group
            label="Наименование"
            label-for="item-name-input"
            label-cols-sm="3"
            label-align-sm="right"
            label-size="sm"
            class="lg"
          >
            <b-input-group size="sm">
              <b-form-input
                id="item-name-input"
                type="text"
                v-model="templateGlossaryItemName"
              >
              </b-form-input>
            </b-input-group>
          </b-form-group>

          <b-form-group
            label="Описание"
            label-for="item-descr-input"
            label-cols-sm="3"
            label-align-sm="right"
            label-size="sm"
            class="lg"
          >
            <b-input-group size="sm">
              <b-form-input
                id="item-descr-input"
                type="text"
                v-model="templateGlossaryItemDescription"
              >
              </b-form-input>
            </b-input-group>
          </b-form-group>

          <b-form-group
            label="Источник"
            label-for="item-link-input"
            label-cols-sm="3"
            label-align-sm="right"
            label-size="sm"
            class="lg"
          >
            <b-input-group size="sm">
              <b-form-input
                id="item-link-input"
                type="url"
                v-model="templateGlossaryItemLink"
              >
              </b-form-input>
            </b-input-group>
          </b-form-group>
        </b-modal>
      </b-col>
    </b-row>

    <b-table
      striped
      hover
      ref="glossary_table"
      :items="items"
      :fields="fields"
      responsive="sm"
      id="glossary-table"
    >
      <template #cell(link)="link">
        <a href="#" @click="redirectToSource(link.value)" style="color: black"
          >Ссылка на источник</a
        >
      </template>
    </b-table>
  </b-container>
</template>

<script lang="ts">
import $ from "jquery";

export default {
  data() {
    return {
      sortOrder: "ASC",
      filter: "",
      templateGlossaryItemName: "",
      templateGlossaryItemDescription: "",
      templateGlossaryItemLink: "",
      fields: [
        {
          key: "name",
          label: "Термин",
          sortable: true,
        },
        { key: "description", label: "Описание" },
        {
          key: "link",
          label: "Источник",
        },
      ],
      items: [],
    };
  },
  mounted() {
    this.loadGlossary();
  },
  methods: {
    loadGlossary() {
      let getGlossaryItemsQuery = `{ 
        getGlossaryItems(sort: ${this.sortOrder}, nameFilter: "${this.filter}") { 
          name description link 
        } 
      }`;

      console.log(getGlossaryItemsQuery);

      fetch("http://localhost:8081/graphql", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Accept: "application/json",
        },
        body: JSON.stringify({ query: getGlossaryItemsQuery }),
      })
        .then((r) => r.json())
        .then((data) => {
          this.items = data.data.getGlossaryItems;

          // @ts-ignore
          this.$refs.glossary_table.refresh();
        });
    },
    clearFilter() {
      // hack due to using not production ready bootstrap for vue 3
      $("#filter-input").val("");
      this.filter = "";
    },
    redirectToSource(link: string) {
      console.log(link);
      window.location.href = link;
    },
    createGlossaryItem() {
      if (!this.templateGlossaryItemName) {
        alert("Необходимо ввести наименование термина!");
        return;
      }

      let upsertGlossaryItemQuery = `mutation {
          upsertGlossaryItem(item: {
              name: "${this.templateGlossaryItemName}",
              description: "${this.templateGlossaryItemDescription}",
              link: "${this.templateGlossaryItemLink}"
          }) { name }
        }`;

      console.log(upsertGlossaryItemQuery);

      fetch("http://localhost:8081/graphql", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Accept: "application/json",
        },
        body: JSON.stringify({ query: upsertGlossaryItemQuery }),
      })
        .then((r) => r.json())
        .then((_) => {
          this.templateGlossaryItemName = "";
          this.templateGlossaryItemLink = "";
          this.templateGlossaryItemDescription = "";

          this.loadGlossary();
        });

      $(
        "#glossary-modal > .modal-dialog > .modal-content > .modal-header > .btn-close"
      ).click();
    },
  },
};
</script>